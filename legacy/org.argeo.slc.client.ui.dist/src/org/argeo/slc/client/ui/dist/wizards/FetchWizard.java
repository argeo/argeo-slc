package org.argeo.slc.client.ui.dist.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.api.NodeUtils;
import org.argeo.api.security.Keyring;
import org.argeo.cms.ArgeoNames;
import org.argeo.cms.ArgeoTypes;
import org.argeo.cms.ui.workbench.util.PrivilegedJob;
import org.argeo.eclipse.ui.jcr.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.ViewerUtils;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoSync;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Defines parameters for the fetch process and run it using a {@link RepoSync}
 * object.
 */
public class FetchWizard extends Wizard {
	// private final static Log log = LogFactory.getLog(FetchWizard.class);

	// Business objects
	private Keyring keyring;
	private RepositoryFactory repositoryFactory;
	private Session currSession;
	private Node targetRepoNode, sourceRepoNode;

	private List<WkspObject> selectedWorkspaces = new ArrayList<WkspObject>();

	// The pages
	private ChooseWkspPage chooseWkspPage;
	private RecapPage recapPage;

	// Cache the advanced pages
	private Map<WkspObject, AdvancedFetchPage> advancedPages = new HashMap<FetchWizard.WkspObject, FetchWizard.AdvancedFetchPage>();

	// Controls with parameters
	private Button filesOnlyBtn;
	private Button advancedBtn;
	private CheckboxTableViewer wkspViewer;

	public FetchWizard(Keyring keyring, RepositoryFactory repositoryFactory,
			Repository nodeRepository) {
		super();
		this.keyring = keyring;
		this.repositoryFactory = repositoryFactory;
		try {
			currSession = nodeRepository.login();
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unexpected error while initializing fetch wizard", e);
		}
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(currSession);
		super.dispose();
	}

	@Override
	public void addPages() {
		try {
			chooseWkspPage = new ChooseWkspPage();
			addPage(chooseWkspPage);
			recapPage = new RecapPage();
			addPage(recapPage);
			setWindowTitle("Define Fetch Procedure");
		} catch (Exception e) {
			throw new SlcException("Cannot add page to wizard ", e);
		}
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;
		try {
			// Target Repository
			String targetRepoUri = targetRepoNode.getProperty(
					ArgeoNames.ARGEO_URI).getString();
			Repository targetRepo = RepoUtils.getRepository(repositoryFactory,
					keyring, targetRepoNode);
			Credentials targetCredentials = RepoUtils.getRepositoryCredentials(
					keyring, targetRepoNode);

			// Source Repository
			String sourceRepoUri = sourceRepoNode.getProperty(
					ArgeoNames.ARGEO_URI).getString();
			Repository sourceRepo = RepoUtils.getRepository(repositoryFactory,
					keyring, sourceRepoNode);
			Credentials sourceCredentials = RepoUtils.getRepositoryCredentials(
					keyring, sourceRepoNode);

			String msg = "Your are about to fetch data from repository: \n\t"
					+ sourceRepoUri + "\ninto target repository: \n\t"
					+ targetRepoUri + "\nDo you really want to proceed ?";

			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm Fetch Launch", msg);

			if (result) {
				RepoSync repoSync = new RepoSync(sourceRepo, sourceCredentials,
						targetRepo, targetCredentials);
				repoSync.setTargetRepoUri(targetRepoUri);
				repoSync.setSourceRepoUri(sourceRepoUri);

				// Specify workspaces to synchronise
				Map<String, String> wksps = new HashMap<String, String>();
				for (Object obj : wkspViewer.getCheckedElements()) {
					WkspObject stn = (WkspObject) obj;
					wksps.put(stn.srcName, stn.targetName);
				}
				repoSync.setWkspMap(wksps);

				// Set the import files only option
				repoSync.setFilesOnly(filesOnlyBtn.getSelection());
				FetchJob job = new FetchJob(repoSync);
				job.setUser(true);
				job.schedule();
			}
		} catch (Exception e) {
			throw new SlcException(
					"Unexpected error while launching the fetch", e);
		}
		return true;
	}

	// ///////////////////////////////
	// ////// THE PAGES

	private class ChooseWkspPage extends WizardPage {
		private static final long serialVersionUID = 211336700788047638L;

		private Map<String, Node> sourceReposMap;
		private Combo chooseSourceRepoCmb;

		public ChooseWkspPage() {
			super("Main");
			setTitle("Choose workspaces to fetch");
			setDescription("Check 'advanced fetch' box to "
					+ "rename workspaces and fine tune the process");

			// Initialise with registered Repositories
			sourceReposMap = getSourceRepoUris();
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NO_FOCUS);
			composite.setLayout(new GridLayout(2, false));

			// Choose source repository combo
			new Label(composite, SWT.NONE)
					.setText("Choose a source repository");
			chooseSourceRepoCmb = new Combo(composite, SWT.BORDER
					| SWT.V_SCROLL);
			chooseSourceRepoCmb.setItems(sourceReposMap.keySet().toArray(
					new String[sourceReposMap.size()]));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			chooseSourceRepoCmb.setLayoutData(gd);

			// Check boxes
			final Button selectAllBtn = new Button(composite, SWT.CHECK);
			selectAllBtn.setText("Select/Unselect all");

			advancedBtn = new Button(composite, SWT.CHECK);
			advancedBtn.setText("Advanced fetch");
			advancedBtn.setToolTipText("Check this for further "
					+ "parameterization of the fetch process");

			// Workspace table
			Table table = new Table(composite, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.BORDER | SWT.CHECK);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.horizontalSpan = 2;
			table.setLayoutData(gd);
			configureWkspTable(table);

			// Import only files
			filesOnlyBtn = new Button(composite, SWT.CHECK | SWT.WRAP);
			filesOnlyBtn
					.setText("Import only files (faster, a normalized action should be launched once done)");
			filesOnlyBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					false, 2, 1));

			// Listeners
			selectAllBtn.addSelectionListener(new SelectionAdapter() {
				private static final long serialVersionUID = -2071840477444152170L;

				public void widgetSelected(SelectionEvent e) {
					wkspViewer.setAllChecked(selectAllBtn.getSelection());
					getContainer().updateButtons();
				}
			});

			// advancedBtn.addSelectionListener(new SelectionAdapter() {
			// public void widgetSelected(SelectionEvent e) {
			// if (advancedBtn.getSelection()){
			//
			// }
			// wkspViewer.setAllChecked();
			// }
			// });

			chooseSourceRepoCmb.addModifyListener(new ModifyListener() {
				private static final long serialVersionUID = 932462568382594523L;

				public void modifyText(ModifyEvent e) {
					String chosenUri = chooseSourceRepoCmb
							.getItem(chooseSourceRepoCmb.getSelectionIndex());
					sourceRepoNode = sourceReposMap.get(chosenUri);
					wkspViewer.setInput(sourceRepoNode);
				}
			});

			wkspViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					getContainer().updateButtons();
				}
			});

			// Initialise to first available repo
			if (chooseSourceRepoCmb.getItemCount() > 0)
				chooseSourceRepoCmb.select(0);

			// Compulsory
			setControl(composite);
		}

		@Override
		public boolean isPageComplete() {
			return wkspViewer.getCheckedElements().length != 0;
		}

		@Override
		public IWizardPage getNextPage() {
			// WARNING: page are added and never removed.
			if (advancedBtn.getSelection()
					&& wkspViewer.getCheckedElements().length != 0) {
				IWizardPage toReturn = null;
				for (Object obj : wkspViewer.getCheckedElements()) {
					WkspObject curr = (WkspObject) obj;
					// currSelecteds.add(curr);
					AdvancedFetchPage page;
					if (!advancedPages.containsKey(curr)) {
						page = new AdvancedFetchPage(curr.srcName, curr);
						addPage(page);
						advancedPages.put(curr, page);
					} else
						page = advancedPages.get(curr);
					if (toReturn == null)
						toReturn = page;
				}
				return toReturn;
			} else {
				return recapPage;
			}
		}

		// Configure the workspace table
		private void configureWkspTable(Table table) {
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			wkspViewer = new CheckboxTableViewer(table);

			// WORKSPACE COLUMNS
			TableViewerColumn column = ViewerUtils.createTableViewerColumn(
					wkspViewer, "Source names", SWT.NONE, 250);
			column.setLabelProvider(new ColumnLabelProvider() {
				private static final long serialVersionUID = 5906079281065061967L;

				@Override
				public String getText(Object element) {
					return ((WkspObject) element).srcName;
				}
			});

			// column = ViewerUtils.createTableViewerColumn(wkspViewer, "Size",
			// SWT.NONE, 250);
			// column.setLabelProvider(new ColumnLabelProvider() {
			// @Override
			// public String getText(Object element) {
			// return ((WkspObject) element).getFormattedSize();
			// }
			// });

			wkspViewer.setContentProvider(new WkspContentProvider());
			// A basic comparator
			wkspViewer.setComparator(new ViewerComparator());
		}
	}

	private class AdvancedFetchPage extends WizardPage {
		private static final long serialVersionUID = 1109183561920445169L;

		private final WkspObject currentWorkspace;

		private Text targetNameTxt;

		protected AdvancedFetchPage(String pageName, WkspObject currentWorkspace) {
			super(pageName);
			this.currentWorkspace = currentWorkspace;
		}

		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible) {
				String msg = "Define advanced parameters to fetch workspace "
						+ currentWorkspace.srcName;
				setMessage(msg);
				targetNameTxt.setText(currentWorkspace.targetName);
			}
			// else
			// currentWorkspace.targetName = targetNameTxt.getText();
		}

		public void createControl(Composite parent) {
			Composite body = new Composite(parent, SWT.NO_FOCUS);
			body.setLayout(new GridLayout(2, false));
			new Label(body, SWT.NONE).setText("Choose a new name");
			targetNameTxt = new Text(body, SWT.BORDER);
			targetNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false));
			setControl(body);
		}

		protected WkspObject getWorkspaceObject() {
			currentWorkspace.targetName = targetNameTxt.getText();
			return currentWorkspace;
		}

		@Override
		public IWizardPage getNextPage() {
			// WARNING: page are added and never removed.
			// IWizardPage toReturn = null;
			// IWizardPage[] pages = ((Wizard) getContainer()).getPages();
			Object[] selected = wkspViewer.getCheckedElements();
			for (int i = 0; i < selected.length - 1; i++) {
				WkspObject curr = (WkspObject) selected[i];
				if (curr.equals(currentWorkspace))
					return advancedPages.get((WkspObject) selected[i + 1]);
			}
			return recapPage;
		}
	}

	private class RecapPage extends WizardPage {
		private static final long serialVersionUID = -7064862323304300989L;
		private TableViewer recapViewer;

		public RecapPage() {
			super("Validate and launch");
			setTitle("Validate and launch");
		}

		@Override
		public boolean isPageComplete() {
			return isCurrentPage();
		}

		public IWizardPage getNextPage() {
			// always last....
			return null;
		}

		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible) {
				try {
					String targetRepoUri = targetRepoNode.getProperty(
							ArgeoNames.ARGEO_URI).getString();
					String sourceRepoUri = sourceRepoNode.getProperty(
							ArgeoNames.ARGEO_URI).getString();

					String msg = "Fetch data from: " + sourceRepoUri
							+ "\ninto target repository: " + targetRepoUri;
					// + "\nDo you really want to proceed ?";
					setMessage(msg);

					// update values that will be used for the fetch
					selectedWorkspaces.clear();

					for (Object obj : wkspViewer.getCheckedElements()) {
						WkspObject curr = (WkspObject) obj;

						if (advancedBtn.getSelection()) {
							AdvancedFetchPage page = advancedPages.get(curr);
							selectedWorkspaces.add(page.getWorkspaceObject());
						} else
							selectedWorkspaces.add(curr);
					}
					recapViewer.setInput(selectedWorkspaces);
					recapViewer.refresh();

				} catch (RepositoryException re) {
					throw new SlcException("Unable to get repositories URIs",
							re);
				}
			}
		}

		public void createControl(Composite parent) {
			Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.BORDER);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			recapViewer = new TableViewer(table);

			// WORKSPACE COLUMNS
			TableViewerColumn column = ViewerUtils.createTableViewerColumn(
					recapViewer, "Sources", SWT.NONE, 250);
			column.setLabelProvider(new ColumnLabelProvider() {
				private static final long serialVersionUID = 3913459002502680377L;

				@Override
				public String getText(Object element) {
					return ((WkspObject) element).srcName;
				}
			});

			column = ViewerUtils.createTableViewerColumn(recapViewer,
					"targets", SWT.NONE, 250);
			column.setLabelProvider(new ColumnLabelProvider() {
				private static final long serialVersionUID = -517920072332563632L;

				@Override
				public String getText(Object element) {
					return ((WkspObject) element).targetName;
				}
			});

			recapViewer.setContentProvider(new IStructuredContentProvider() {
				private static final long serialVersionUID = 4926999891003040865L;

				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					// TODO Auto-generated method stub
				}

				public void dispose() {
				}

				public Object[] getElements(Object inputElement) {
					return selectedWorkspaces.toArray();
				}
			});

			// A basic comparator
			recapViewer.setComparator(new ViewerComparator());
			setControl(table);
		}
	}

	/**
	 * Define the privileged job that will be run asynchronously to accomplish
	 * the sync
	 */
	private class FetchJob extends PrivilegedJob {
		private RepoSync repoSync;

		public FetchJob(RepoSync repoSync) {
			super("Fetch");
			this.repoSync = repoSync;
		}

		@Override
		protected IStatus doRun(IProgressMonitor progressMonitor) {
			try {
				JcrMonitor monitor = new EclipseJcrMonitor(progressMonitor);
				repoSync.setMonitor(monitor);
				repoSync.run();
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DistPlugin.PLUGIN_ID,
						"Cannot fetch repository", e);
			}
			return Status.OK_STATUS;
		}
	}

	// ///////////////////////
	// Local classes
	private class WkspObject {
		protected final String srcName;
		protected String targetName;

		protected WkspObject(String srcName) {
			this.srcName = srcName;
			this.targetName = srcName;
		}

		@Override
		public String toString() {
			return "[" + srcName + " to " + targetName + "]";
		}
	}

	// private class WkspComparator extends ViewerComparator {
	//
	// }

	private class WkspContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = -925058051598536307L;
		// caches current repo
		private Node currSourceNodeRepo;
		private Repository currSourceRepo;
		private Credentials currSourceCred;

		private List<WkspObject> workspaces = new ArrayList<WkspObject>();

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null && newInput instanceof Node) {
				Session session = null;
				try {
					Node newRepoNode = (Node) newInput;
					if (currSourceNodeRepo == null
							|| !newRepoNode.getPath().equals(
									currSourceNodeRepo.getPath())) {

						// update cache
						currSourceNodeRepo = newRepoNode;
						currSourceRepo = RepoUtils.getRepository(
								repositoryFactory, keyring, currSourceNodeRepo);
						currSourceCred = RepoUtils.getRepositoryCredentials(
								keyring, currSourceNodeRepo);

						// reset workspace list
						wkspViewer.setAllChecked(false);
						workspaces.clear();
						// FIXME make it more generic
						session = currSourceRepo.login(currSourceCred,RepoConstants.DEFAULT_DEFAULT_WORKSPACE);
						// remove unvalid elements
						for (String name : session.getWorkspace()
								.getAccessibleWorkspaceNames())
							// TODO implement a cleaner way to filter
							// workspaces out
							if (name.lastIndexOf('-') > 0) {
								WkspObject currWksp = new WkspObject(name);
								// compute wkspace size
								// TODO implement this
								// Session currSession = null;
								// try {
								// currSession = currSourceRepo.login(
								// currSourceCred, name);
								// currWksp.size = JcrUtils
								// .getNodeApproxSize(currSession
								// .getNode("/"));
								//
								// } catch (RepositoryException re) {
								// log.warn(
								// "unable to compute size of workspace "
								// + name, re);
								// } finally {
								// JcrUtils.logoutQuietly(currSession);
								// }
								workspaces.add(currWksp);
							}
					}

				} catch (RepositoryException e) {
					throw new SlcException("Unexpected error while "
							+ "initializing fetch wizard", e);
				} finally {
					JcrUtils.logoutQuietly(session);
				}
				viewer.refresh();
			}
		}

		public void dispose() {
		}

		public Object[] getElements(Object obj) {
			return workspaces.toArray();
		}
	}

	// ////////////////////////////
	// // Helpers

	// populate available source repo list
	private Map<String, Node> getSourceRepoUris() {
		try {
			Node repoList = currSession.getNode(NodeUtils.getUserHome(
					currSession).getPath()
					+ RepoConstants.REPOSITORIES_BASE_PATH);

			String targetRepoUri = null;
			if (targetRepoNode != null) {
				targetRepoUri = targetRepoNode
						.getProperty(ArgeoNames.ARGEO_URI).getString();
			}
			NodeIterator ni = repoList.getNodes();
			// List<String> sourceRepoNames = new ArrayList<String>();
			// // caches a map of the source repo nodes with their URI as a key
			// // to ease further processing
			Map<String, Node> sourceReposMap = new HashMap<String, Node>();
			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (currNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
					String currUri = currNode.getProperty(ArgeoNames.ARGEO_URI)
							.getString();
					if (targetRepoUri == null || !targetRepoUri.equals(currUri)) {
						sourceReposMap.put(currUri, currNode);
						// sourceRepoNames.add(currUri);
					}
				}
			}
			return sourceReposMap;
			// sourceRepoNames.toArray(new String[sourceRepoNames
			// .size()]);
		} catch (RepositoryException e) {
			throw new SlcException("Error while getting repo aliases", e);
		}
	}

	public void setTargetRepoNode(Node targetRepoNode) {
		this.targetRepoNode = targetRepoNode;
	}

	public void setSourceRepoNode(Node sourceRepoNode) {
		this.sourceRepoNode = sourceRepoNode;
	}
}