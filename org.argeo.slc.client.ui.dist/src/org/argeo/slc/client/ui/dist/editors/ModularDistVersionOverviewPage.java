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
package org.argeo.slc.client.ui.dist.editors;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.EclipseUiUtils;
import org.argeo.eclipse.ui.workbench.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.commands.OpenModuleEditor;
import org.argeo.slc.client.ui.dist.utils.DistNodeViewerComparator;
import org.argeo.slc.client.ui.dist.utils.HyperlinkAdapter;
//import org.argeo.slc.client.ui.specific.OpenJcrFile;
//import org.argeo.slc.client.ui.specific.OpenJcrFileCmdId;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Show all modules contained in a given modular distribution as filter-able
 * table
 */
public class ModularDistVersionOverviewPage extends FormPage implements
		SlcNames {

	private final static Log log = LogFactory
			.getLog(ModularDistVersionOverviewPage.class);

	final static String PAGE_ID = "ModularDistVersionOverviewPage";

	// Business Objects
	private Node modularDistribution;
	// private Node modularDistributionBase;

	// This page widgets
	private DistNodeViewerComparator comparator;
	private TableViewer viewer;
	private FormToolkit tk;
	private Text filterTxt;
	private final static String FILTER_HELP_MSG = "Enter filter criterion separated by a space";

	public ModularDistVersionOverviewPage(FormEditor formEditor, String title,
			Node modularDistribution) {
		super(formEditor, PAGE_ID, title);
		this.modularDistribution = modularDistribution;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		// General settings for this page
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();
		Composite body = form.getBody();

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 5;
		layout.marginRight = 15;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		try {
			form.setText(modularDistribution.hasProperty(SlcNames.SLC_NAME) ? modularDistribution
					.getProperty(SlcNames.SLC_NAME).getString() : "");
			form.setMessage(
					modularDistribution
							.hasProperty(DistConstants.SLC_BUNDLE_DESCRIPTION) ? modularDistribution
							.getProperty(DistConstants.SLC_BUNDLE_DESCRIPTION)
							.getString() : "", IMessageProvider.NONE);
		} catch (RepositoryException re) {
			throw new SlcException("Unable to get bundle name for node "
					+ modularDistribution, re);
		}

		// Main layout
		Composite header = tk.createComposite(body);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		populateHeaderPart(header);

		Composite moduleTablePart = tk.createComposite(body);
		moduleTablePart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		populateModuleTablePart(moduleTablePart);
	}

	private void populateHeaderPart(Composite parent) {
		GridLayout layout = new GridLayout(6, false);
		layout.horizontalSpacing = 10;
		parent.setLayout(layout);
		try {
			// 1st Line: Category, name version
			createLT(
					parent,
					"Category",
					modularDistribution.hasProperty(SlcNames.SLC_CATEGORY) ? modularDistribution
							.getProperty(SlcNames.SLC_CATEGORY).getString()
							: "");
			createLT(
					parent,
					"Name",
					modularDistribution.hasProperty(SlcNames.SLC_NAME) ? modularDistribution
							.getProperty(SlcNames.SLC_NAME).getString() : "");
			createLT(
					parent,
					"Version",
					modularDistribution.hasProperty(SlcNames.SLC_VERSION) ? modularDistribution
							.getProperty(SlcNames.SLC_VERSION).getString() : "");

			// 2nd Line: Vendor, licence, sources
			createLT(
					parent,
					"Vendor",
					modularDistribution
							.hasProperty(DistConstants.SLC_BUNDLE_VENDOR) ? modularDistribution
							.getProperty(DistConstants.SLC_BUNDLE_VENDOR)
							.getString() : "N/A");

			createHyperlink(parent, "Licence", DistConstants.SLC_BUNDLE_LICENCE);
			addSourceSourcesLink(parent);
		} catch (RepositoryException re) {
			throw new SlcException("Unable to get bundle name for node "
					+ modularDistribution, re);
		}

	}

	private Text createLT(Composite parent, String labelValue, String textValue) {
		Label label = tk.createLabel(parent, labelValue, SWT.RIGHT);
		// label.setFont(EclipseUiUtils.getBoldFont(parent));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		// Add a trailing space to workaround a display glitch in RAP 1.3
		Text text = new Text(parent, SWT.LEFT);
		text.setText(textValue + " ");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setEditable(false);
		return text;
	}

	private void createHyperlink(Composite parent, String label,
			String jcrPropName) throws RepositoryException {
		tk.createLabel(parent, label, SWT.NONE);
		if (modularDistribution.hasProperty(jcrPropName)) {
			final Hyperlink link = tk.createHyperlink(parent,
					modularDistribution.getProperty(jcrPropName).getString(),
					SWT.NONE);
			link.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					try {
						IWorkbenchBrowserSupport browserSupport = PlatformUI
								.getWorkbench().getBrowserSupport();
						IWebBrowser browser = browserSupport
								.createBrowser(
										IWorkbenchBrowserSupport.LOCATION_BAR
												| IWorkbenchBrowserSupport.NAVIGATION_BAR,
										"SLC Distribution browser",
										"SLC Distribution browser",
										"A tool tip");
						browser.openURL(new URL(link.getText()));
					} catch (Exception ex) {
						throw new SlcException("error opening browser", ex); //$NON-NLS-1$
					}
				}
			});
		} else
			tk.createLabel(parent, "N/A", SWT.NONE);
	}

	// helper to check if sources are available
	private void addSourceSourcesLink(Composite parent) {
		try {
			String srcPath = RepoUtils.relatedPdeSourcePath(
					RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH,
					modularDistribution);
			if (!modularDistribution.getSession().nodeExists(srcPath)) {
				createLT(parent, "Sources", "N/A");
			} else {
				final Node sourcesNode = modularDistribution.getSession()
						.getNode(srcPath);

				String srcName = null;
				if (sourcesNode.hasProperty(SlcNames.SLC_SYMBOLIC_NAME))
					srcName = sourcesNode.getProperty(
							SlcNames.SLC_SYMBOLIC_NAME).getString();
				else
					srcName = sourcesNode.getName();
				Label label = tk.createLabel(parent, "Sources", SWT.RIGHT);
				label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
						false));
				Hyperlink link = tk.createHyperlink(parent, srcName, SWT.NONE);
				link.addHyperlinkListener(new OpenFileLinkListener(sourcesNode
						.getPath()));
			}
		} catch (RepositoryException e) {
			throw new SlcException("Unable to configure sources link for "
					+ modularDistribution, e);
		}
	}

	private class OpenFileLinkListener extends HyperlinkAdapter {
		final private String path;

		public OpenFileLinkListener(String path) {
			this.path = path;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			log.warn("File download must be implemented. Cannot provide access to "
					+ path);

			// try {
			// ModuleEditorInput editorInput = (ModuleEditorInput)
			// getEditorInput();
			// Map<String, String> params = new HashMap<String, String>();
			// params.put(OpenJcrFile.PARAM_REPO_NODE_PATH,
			// editorInput.getRepoNodePath());
			// params.put(OpenJcrFile.PARAM_REPO_URI, editorInput.getUri());
			// params.put(OpenJcrFile.PARAM_WORKSPACE_NAME,
			// editorInput.getWorkspaceName());
			// params.put(OpenJcrFile.PARAM_FILE_PATH, path);
			//
			// String cmdId = (new OpenJcrFileCmdId()).getCmdId();
			// CommandUtils.callCommand(cmdId, params);
			// } catch (Exception ex) {
			//				throw new SlcException("error opening browser", ex); //$NON-NLS-1$
			// }
		}
	}

	private void populateModuleTablePart(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.horizontalSpacing = 0;
		layout.verticalSpacing = 5;
		layout.marginTop = 15;
		parent.setLayout(layout);
		// A sub title
		Label label = tk.createLabel(parent,
				"Modules included in the current distribution", SWT.NONE);
		label.setFont(EclipseUiUtils.getBoldFont(parent));

		// Add the filter section
		Composite filterPart = tk.createComposite(parent);
		filterPart.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createFilterPart(filterPart);

		// Add the table
		Composite tablePart = tk.createComposite(parent);
		tablePart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createTableViewer(tablePart);
		// populate it on first pass.
		refresh();
	}

	private void createFilterPart(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;
		parent.setLayout(layout);

		// Text Area to filter
		filterTxt = tk.createText(parent, "", SWT.BORDER | SWT.SINGLE
				| SWT.SEARCH | SWT.CANCEL);
		filterTxt.setMessage(FILTER_HELP_MSG);
		filterTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterTxt.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = -276152321986407726L;

			public void modifyText(ModifyEvent event) {
				refresh();
			}
		});

		Button resetBtn = tk.createButton(parent, null, SWT.PUSH);
		resetBtn.setImage(DistImages.IMG_CLEAR);
		resetBtn.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -3549303742841670919L;

			public void widgetSelected(SelectionEvent e) {
				filterTxt.setText("");
				filterTxt.setMessage(FILTER_HELP_MSG);
			}
		});
	}

	private void createTableViewer(Composite parent) {
		parent.setLayout(new FillLayout());
		// helpers to enable sorting by column
		List<String> propertiesList = new ArrayList<String>();
		List<Integer> propertyTypesList = new ArrayList<Integer>();

		// Define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(220);
		col.getColumn().setText("Category");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 5875398301711336875L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SlcNames.SLC_CATEGORY);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(0));
		propertiesList.add(SlcNames.SLC_CATEGORY);
		propertyTypesList.add(PropertyType.STRING);

		// Symbolic name
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(220);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 3880240676256465072L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(1));
		propertiesList.add(SLC_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(160);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -4706438113850571784L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_VERSION);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(2));
		propertiesList.add(SLC_VERSION);
		propertyTypesList.add(DistNodeViewerComparator.VERSION_TYPE);

		// Exists in workspace
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(160);
		col.getColumn().setText("Exists in workspace");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 8190063212920414300L;

			@Override
			public String getText(Object element) {
				return getRealizedModule((Node) element) != null ? "Yes" : "No";
				// return JcrUtils.get((Node) element, SLC_VERSION);
			}
		});
		// col.getColumn().addSelectionListener(getSelectionAdapter(2));
		// propertiesList.add(SLC_VERSION);
		// propertyTypesList.add(PropertyType.STRING);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new DistributionsContentProvider());
		getSite().setSelectionProvider(viewer);

		comparator = new DistNodeViewerComparator(2,
				DistNodeViewerComparator.ASCENDING, propertiesList,
				propertyTypesList);
		viewer.setComparator(comparator);

		// // Context Menu
		// MenuManager menuManager = new MenuManager();
		// Menu menu = menuManager.createContextMenu(viewer.getTable());
		// menuManager.addMenuListener(new IMenuListener() {
		// public void menuAboutToShow(IMenuManager manager) {
		// contextMenuAboutToShow(manager);
		// }
		// });
		// viewer.getTable().setMenu(menu);
		// getSite().registerContextMenu(menuManager, viewer);

		// Double click
		viewer.addDoubleClickListener(new DoubleClickListener());
	}

	private Node getRealizedModule(Node moduleCoordinates) {
		try {
			String category = JcrUtils.get(moduleCoordinates, SLC_CATEGORY);
			String name = JcrUtils.get(moduleCoordinates, SLC_NAME);
			String version = JcrUtils.get(moduleCoordinates, SLC_VERSION);
			Artifact artifact = new DefaultArtifact(category + ":" + name + ":"
					+ version);
			String parentPath = MavenConventionsUtils.artifactParentPath(
					RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH, artifact);

			Session session = modularDistribution.getSession();
			if (session.nodeExists(parentPath)) {
				Node parent = session.getNode(parentPath);
				NodeIterator nit = parent.getNodes();
				while (nit.hasNext()) {
					Node currN = nit.nextNode();
					if (currN.isNodeType(SlcTypes.SLC_ARTIFACT))
						return currN;
				}
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"unable to retrieve realized module with coordinates "
							+ moduleCoordinates, re);
		}
		return null;
	}

	private void refresh() {
		final List<Node> result = JcrUtils
				.nodeIteratorToList(listBundleArtifacts());
		viewer.setInput(result);
	}

	/** Build repository request */
	private NodeIterator listBundleArtifacts() {
		try {
			Session session = modularDistribution.getSession();
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();

			Selector source = factory.selector(SlcTypes.SLC_MODULE_COORDINATES,
					SlcTypes.SLC_MODULE_COORDINATES);

			// Create a dynamic operand for each property on which we want to
			// filter
			DynamicOperand catDO = factory.propertyValue(
					source.getSelectorName(), SlcNames.SLC_CATEGORY);
			DynamicOperand nameDO = factory.propertyValue(
					source.getSelectorName(), SlcNames.SLC_NAME);
			DynamicOperand versionDO = factory.propertyValue(
					source.getSelectorName(), SlcNames.SLC_VERSION);

			String path = modularDistribution.getPath() + "/"
					+ SlcNames.SLC_MODULES;

			// Default Constraint: correct children
			Constraint defaultC = factory.descendantNode(
					source.getSelectorName(), path);

			String filter = filterTxt.getText();

			// Build constraints based the textArea content
			if (filter != null && !"".equals(filter.trim())) {
				// Parse the String
				String[] strs = filter.trim().split(" ");
				for (String token : strs) {
					token = token.replace('*', '%');
					StaticOperand so = factory.literal(session
							.getValueFactory().createValue("%" + token + "%"));

					Constraint currC = factory.comparison(catDO,
							QueryObjectModelFactory.JCR_OPERATOR_LIKE, so);
					currC = factory.or(currC, factory.comparison(versionDO,
							QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));
					currC = factory.or(currC, factory.comparison(nameDO,
							QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));

					defaultC = factory.and(defaultC, currC);
				}
			}

			QueryObjectModel query = factory.createQuery(source, defaultC,
					null, null);
			QueryResult result = query.execute();
			return result.getNodes();
		} catch (RepositoryException re) {
			throw new SlcException("Unable to refresh module list for node "
					+ modularDistribution, re);
		}
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	// /** Programmatically configure the context menu */
	// protected void contextMenuAboutToShow(IMenuManager menuManager) {
	// IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
	// .getActiveWorkbenchWindow();
	// // Build conditions
	// // Delete selected artifacts
	// // CommandUtils.refreshCommand(menuManager, window, DeleteArtifacts.ID,
	// // DeleteArtifacts.DEFAULT_LABEL, DeleteArtifacts.DEFAULT_ICON,
	// // true);
	// }

	private SelectionAdapter getSelectionAdapter(final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 1260801795934660840L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				comparator.setColumn(index);
				int dir = table.getSortDirection();
				if (table.getSortColumn() == table.getColumn(index)) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				table.setSortDirection(dir);
				table.setSortColumn(table.getColumn(index));
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	/* LOCAL CLASSES */
	private class DistributionsContentProvider implements
			IStructuredContentProvider {
		private static final long serialVersionUID = 8385338190908823791L;
		// we keep a cache of the Nodes in the content provider to be able to
		// manage long request
		private List<Node> nodes;

		public void dispose() {
		}

		// We expect a list of nodes as a new input
		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			nodes = (List<Node>) newInput;
		}

		public Object[] getElements(Object arg0) {
			return nodes.toArray();
		}
	}

	private class DoubleClickListener implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			Object obj = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			if (obj instanceof Node) {
				Node node = (Node) obj;
				try {
					if (node.isNodeType(SlcTypes.SLC_MODULE_COORDINATES)) {
						Node realizedModule = getRealizedModule(node);
						if (realizedModule != null) {
							ModuleEditorInput dwip = (ModuleEditorInput) getEditorInput();
							Map<String, String> params = new HashMap<String, String>();
							params.put(OpenModuleEditor.PARAM_REPO_NODE_PATH,
									dwip.getRepoNodePath());
							params.put(OpenModuleEditor.PARAM_REPO_URI,
									dwip.getUri());
							params.put(OpenModuleEditor.PARAM_WORKSPACE_NAME,
									dwip.getWorkspaceName());
							String path = realizedModule.getPath();
							params.put(OpenModuleEditor.PARAM_MODULE_PATH, path);
							CommandUtils.callCommand(OpenModuleEditor.ID,
									params);
						}
					}
				} catch (RepositoryException re) {
					throw new SlcException("Cannot get path for node " + node
							+ " while setting parameters for "
							+ "command OpenModuleEditor", re);
				}
			}
		}
	}
}