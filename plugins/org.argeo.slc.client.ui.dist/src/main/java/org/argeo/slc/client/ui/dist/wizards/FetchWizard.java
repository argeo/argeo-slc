/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.argeo.ArgeoMonitor;
import org.argeo.eclipse.ui.EclipseArgeoMonitor;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.PrivilegedJob;
import org.argeo.slc.client.ui.dist.utils.ArtifactNamesComparator;
import org.argeo.slc.client.ui.dist.utils.ViewerUtils;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoSync;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

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

	// This page widget
	private DefineModelPage page;
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
		super.dispose();
		JcrUtils.logoutQuietly(currSession);
	}

	@Override
	public void addPages() {
		try {
			page = new DefineModelPage();
			addPage(page);
			setWindowTitle("Fetch...");
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

			String msg;

			// no workspace has been chosen, we return
			if (wkspViewer.getCheckedElements().length == 0) {
				msg = "No workspace has been chosen, and thus no fetch has been done.";
				MessageDialog.openWarning(DistPlugin.getDefault()
						.getWorkbench().getDisplay().getActiveShell(),
						"Warning", msg);
				return true;
			}

			msg = "Your are about to fetch data from repository: \n\t"
					+ sourceRepoUri + "\ninto target repository: \n\t"
					+ targetRepoUri + "\nDo you really want to proceed ?";

			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm Fetch clear", msg);

			if (result) {
				RepoSync repoSync = new RepoSync(sourceRepo, sourceCredentials,
						targetRepo, targetCredentials);
				repoSync.setTargetRepoUri(targetRepoUri);
				repoSync.setSourceRepoUri(sourceRepoUri);

				// Specify workspaces to synchronise
				List<String> wksps = new ArrayList<String>();
				for (Object obj : wkspViewer.getCheckedElements()) {
					wksps.add((String) obj);
				}
				repoSync.setSourceWkspList(wksps);
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

	private class DefineModelPage extends WizardPage {

		// This page widget
		private Combo chooseSourceRepoCmb;

		// Business objects
		private Map<String, Node> sourceReposMap;

		public DefineModelPage() {
			super("Main");
			setTitle("Define fetch parameters");
		}

		public void createControl(Composite parent) {

			// main layout
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			// Choose source repo
			new Label(composite, SWT.NONE)
					.setText("Choose a source repository");
			chooseSourceRepoCmb = new Combo(composite, SWT.BORDER
					| SWT.V_SCROLL);
			chooseSourceRepoCmb.setItems(getSourceRepoUris());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			chooseSourceRepoCmb.setLayoutData(gd);

			// Workspace table
			Composite wkspTable = new Composite(composite, SWT.NONE);
			gd = new GridData();
			gd.horizontalSpan = 2;
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			wkspTable.setLayoutData(gd);
			wkspTable.setLayout(new GridLayout(1, false));
			addWkspTablePart(wkspTable);

			// Choose source repo
			final Button selectAllBtn = new Button(composite, SWT.CHECK);
			selectAllBtn.setText("Select/Unselect all");

			selectAllBtn.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					wkspViewer.setAllChecked(selectAllBtn.getSelection());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			chooseSourceRepoCmb.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String chosenUri = chooseSourceRepoCmb
							.getItem(chooseSourceRepoCmb.getSelectionIndex());
					sourceRepoNode = sourceReposMap.get(chosenUri);
					wkspViewer.setInput(sourceRepoNode);
				}
			});

			// initialize to first avalaible repo
			if (chooseSourceRepoCmb.getItemCount() > 0)
				chooseSourceRepoCmb.select(0);
			// Compulsory
			setControl(composite);
		}

		// Helper to populate avalaible source repo list
		protected String[] getSourceRepoUris() {
			try {
				Node repoList = currSession.getNode(UserJcrUtils.getUserHome(
						currSession).getPath()
						+ RepoConstants.REPOSITORIES_BASE_PATH);

				String targetRepoUri = null;
				if (targetRepoNode != null) {
					targetRepoUri = targetRepoNode.getProperty(
							ArgeoNames.ARGEO_URI).getString();
				}
				NodeIterator ni = repoList.getNodes();
				List<String> sourceRepoNames = new ArrayList<String>();
				// caches a map of the source repo nodes with their URI as a key
				// to ease further processing
				sourceReposMap = new HashMap<String, Node>();
				while (ni.hasNext()) {
					Node currNode = ni.nextNode();
					if (currNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
						String currUri = currNode.getProperty(
								ArgeoNames.ARGEO_URI).getString();
						if (targetRepoUri == null
								|| !targetRepoUri.equals(currUri)) {
							sourceReposMap.put(currUri, currNode);
							sourceRepoNames.add(currUri);
						}
					}
				}
				return sourceRepoNames.toArray(new String[sourceRepoNames
						.size()]);
			} catch (RepositoryException e) {
				throw new SlcException("Error while getting repo aliases", e);
			}
		}

		// Create the workspaces table
		private void addWkspTablePart(Composite parent) {

			Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.BORDER | SWT.CHECK);
			table.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			wkspViewer = new CheckboxTableViewer(table);

			// WORKSPACES COLUMN
			TableViewerColumn column = ViewerUtils.createTableViewerColumn(
					wkspViewer, "Workspaces", SWT.NONE, 400);
			column.setLabelProvider(new ColumnLabelProvider());

			wkspViewer.setContentProvider(new IStructuredContentProvider() {
				// caches current repo
				private Repository currSourceRepo;
				private Credentials currSourceCred;

				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					if (newInput != null && newInput instanceof Node) {
						// update current used repository
						currSourceRepo = RepoUtils.getRepository(
								repositoryFactory, keyring, (Node) newInput);
						currSourceCred = RepoUtils.getRepositoryCredentials(
								keyring, (Node) newInput);
						// reset workspace list
						wkspViewer.setAllChecked(false);
					}
				}

				public void dispose() {
				}

				public Object[] getElements(Object obj) {
					Session session = null;
					try {
						session = currSourceRepo.login(currSourceCred);
						List<String> result = new ArrayList<String>();
						// remove unvalid elements
						for (String name : session.getWorkspace()
								.getAccessibleWorkspaceNames())
							if (name.lastIndexOf('-') > 0)
								result.add(name);
						return result.toArray();
					} catch (RepositoryException e) {
						throw new SlcException(
								"Unexpected error while initializing fetch wizard",
								e);
					} finally {
						JcrUtils.logoutQuietly(session);
					}
				}
			});
			wkspViewer.setComparator(new ArtifactNamesComparator());

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
				ArgeoMonitor monitor = new EclipseArgeoMonitor(progressMonitor);
				repoSync.setMonitor(monitor);
				repoSync.run();
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DistPlugin.ID,
						"Cannot fetch repository", e);
			}
			return Status.OK_STATUS;
		}
	}

	public void setTargetRepoNode(Node targetRepoNode) {
		this.targetRepoNode = targetRepoNode;
	}

	public void setSourceRepoNode(Node sourceRepoNode) {
		this.sourceRepoNode = sourceRepoNode;
	}
}