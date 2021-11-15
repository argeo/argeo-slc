package org.argeo.slc.client.ui.dist.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.argeo.cms.ui.workbench.util.CommandUtils;
import org.argeo.cms.ui.workbench.util.PrivilegedJob;
import org.argeo.eclipse.ui.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.DeleteArtifacts;
import org.argeo.slc.client.ui.dist.commands.OpenModuleEditor;
import org.argeo.slc.client.ui.dist.utils.DistNodeViewerComparator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/** Show all bundles contained in a given workspace as filter-able table */
public class DistWkspSearchPage extends FormPage implements SlcNames {
	// final private static Log log = LogFactory
	// .getLog(DistributionOverviewPage.class);

	final static String PAGE_ID = "distributionOverviewPage";

	// Business Objects
	private Session session;

	// This page widgets
	private DistWorkspaceEditor formEditor;
	private FormToolkit tk;

	private DistNodeViewerComparator comparator;
	private TableViewer viewer;

	// private Composite header;
	private Text artifactTxt;
	private final static String FILTER_HELP_MSG = "Filter criterion, separated by a space";

	public DistWkspSearchPage(DistWorkspaceEditor formEditor, String title,
			Session session) {
		super(formEditor, PAGE_ID, title);
		this.formEditor = formEditor;
		this.session = session;
	}

	private void asynchronousRefresh() {
		RefreshJob job = new RefreshJob(artifactTxt.getText(), viewer,
				getSite().getShell().getDisplay());
		job.setUser(true);
		job.schedule();
	}

	private class RefreshJob extends PrivilegedJob {
		private TableViewer viewer;
		private String filter;
		private Display display;

		public RefreshJob(String filter, TableViewer viewer, Display display) {
			super("Get bundle list");
			this.filter = filter;
			this.viewer = viewer;
			this.display = display;
		}

		@Override
		protected IStatus doRun(IProgressMonitor progressMonitor) {
			try {
				JcrMonitor monitor = new EclipseJcrMonitor(progressMonitor);
				monitor.beginTask("Getting bundle list", -1);
				final List<Node> result = JcrUtils
						.nodeIteratorToList(listBundleArtifacts(session, filter));

				display.asyncExec(new Runnable() {
					public void run() {
						viewer.setInput(result);
					}
				});
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DistPlugin.PLUGIN_ID,
						"Cannot get bundle list", e);
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();

		// Main Layout
		GridLayout layout = new GridLayout(1, false);
		Composite body = form.getBody();
		body.setLayout(layout);

		// Meta info about current workspace
		Composite header = tk.createComposite(body);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createHeaderPart(form, header);

		Composite modules = tk.createComposite(body);
		modules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		populateModuleSection(modules);
	}

	private void createHeaderPart(ScrolledForm form, Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		// layout.marginWidth = layout.marginHeight = layout.verticalSpacing =
		// 0;
		// layout.horizontalSpacing = 2;
		parent.setLayout(layout);

		String wkspName = ((DistWkspEditorInput) getEditorInput())
				.getWorkspaceName();
		wkspName = wkspName.replaceAll("-", " ");
		form.setText(wkspName);

		String repoAlias = "";
		Node repoNode = ((DistWorkspaceEditor) getEditor()).getRepoNode();
		if (repoNode != null)
			try {
				repoAlias = repoNode.isNodeType(NodeType.MIX_TITLE) ? repoNode
						.getProperty(Property.JCR_TITLE).getString() : repoNode
						.getName();
			} catch (RepositoryException e1) {
				throw new SlcException("Unable to get repository alias ", e1);
			}
		else
			repoAlias = " - ";

		createLT(parent, "Repository alias", repoAlias);
		createLT(parent, "URI",
				((DistWkspEditorInput) getEditorInput()).getUri());
	}

	private void populateModuleSection(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.horizontalSpacing = layout.horizontalSpacing = layout.marginHeight = 0;
		parent.setLayout(layout);

		Section section = tk.createSection(parent, Section.TITLE_BAR
				| Section.DESCRIPTION);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		section.setText("Artifacts");
		section.setDescription("Search among all artifacts that are referenced in the current workspace");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite body = tk.createComposite(section);
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.horizontalSpacing = layout.horizontalSpacing = layout.marginHeight = 0;
		body.setLayout(new GridLayout());

		// Filter
		Composite filter = tk.createComposite(body);
		filter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createFilterPart(filter);

		// Table
		Composite tableCmp = tk.createComposite(body);
		tableCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createTableViewer(tableCmp);

		section.setClient(body);
	}

	/** Build repository request */
	private NodeIterator listBundleArtifacts(Session session, String filter)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String bundleArtifactsSelector = "bundleArtifacts";
		Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
				bundleArtifactsSelector);

		// Create a dynamic operand for each property on which we want to filter
		DynamicOperand symbNameDO = factory.propertyValue(
				source.getSelectorName(), SlcNames.SLC_SYMBOLIC_NAME);
		DynamicOperand versionDO = factory.propertyValue(
				source.getSelectorName(), SlcNames.SLC_BUNDLE_VERSION);
		DynamicOperand nameDO = factory.propertyValue(source.getSelectorName(),
				DistConstants.SLC_BUNDLE_NAME);

		// Default Constraint: no source artifacts
		Constraint defaultC = factory.not(factory.comparison(
				symbNameDO,
				QueryObjectModelFactory.JCR_OPERATOR_LIKE,
				factory.literal(session.getValueFactory().createValue(
						"%.source"))));

		// Build constraints based the textArea content
		if (filter != null && !"".equals(filter.trim())) {
			// Parse the String
			String[] strs = filter.trim().split(" ");
			for (String token : strs) {
				token = token.replace('*', '%');
				StaticOperand so = factory.literal(session.getValueFactory()
						.createValue("%" + token + "%"));

				Constraint currC = factory.comparison(symbNameDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so);
				currC = factory.or(currC, factory.comparison(versionDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));
				currC = factory.or(currC, factory.comparison(nameDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));

				defaultC = factory.and(defaultC, currC);
			}
		}

		Ordering order = factory.descending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_BUNDLE_VERSION));
		Ordering order2 = factory.ascending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
		Ordering[] orderings = { order, order2 };

		QueryObjectModel query = factory.createQuery(source, defaultC,
				orderings, null);

		QueryResult result = query.execute();
		return result.getNodes();

	}

	private Text createLT(Composite parent, String labelValue, String textValue) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labelValue);
		// label.setFont(EclipseUiUtils.getBoldFont(parent));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		// Add a trailing space to workaround a display glitch in RAP 1.3
		Text text = new Text(parent, SWT.LEFT); // | SWT.BORDER
		text.setText(textValue + " ");
		text.setEditable(false);
		return text;
	}

	private void createFilterPart(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;
		parent.setLayout(layout);

		// Text Area to filter
		artifactTxt = tk.createText(parent, "", SWT.BORDER | SWT.SINGLE
				| SWT.SEARCH | SWT.CANCEL);
		artifactTxt.setMessage(FILTER_HELP_MSG);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.grabExcessHorizontalSpace = true;
		artifactTxt.setLayoutData(gd);
		artifactTxt.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = -2422321852703208573L;

			public void modifyText(ModifyEvent event) {
				if ("".equals(artifactTxt.getText().trim()))
					asynchronousRefresh();
				else
					refreshFilteredList();
			}
		});
	}

	private void refreshFilteredList() {
		List<Node> nodes;
		try {
			nodes = JcrUtils.nodeIteratorToList(listBundleArtifacts(session,
					artifactTxt.getText()));
			viewer.setInput(nodes);
		} catch (RepositoryException e) {
			throw new SlcException("Unable to list bundles", e);
		}
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
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -760226161605987538L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element,
						DistConstants.SLC_BUNDLE_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(0));
		propertiesList.add(DistConstants.SLC_BUNDLE_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Symbolic name
		col = new TableViewerColumn(viewer, SWT.V_SCROLL);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Symbolic Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 4431447542158431355L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(1));
		propertiesList.add(SLC_SYMBOLIC_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(130);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -5616215547236158504L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(2));
		propertiesList.add(SLC_BUNDLE_VERSION);
		propertyTypesList.add(DistNodeViewerComparator.VERSION_TYPE);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new DistributionsContentProvider());
		getSite().setSelectionProvider(viewer);

		comparator = new DistNodeViewerComparator(2,
				DistNodeViewerComparator.ASCENDING, propertiesList,
				propertyTypesList);
		viewer.setComparator(comparator);

		// Context Menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		menuManager.addMenuListener(new IMenuListener() {
			private static final long serialVersionUID = -3886983092940055195L;

			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		// Double click
		viewer.addDoubleClickListener(new DoubleClickListener());
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	/** force refresh of the artifact list */
	public void refresh() {
		asynchronousRefresh();
	}

	/** Programmatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		// Build conditions
		// Delete selected artifacts
		CommandUtils.refreshCommand(menuManager, window, DeleteArtifacts.ID,
				DeleteArtifacts.DEFAULT_LABEL, DeleteArtifacts.DEFAULT_ICON,
				true);
	}

	private SelectionAdapter getSelectionAdapter(final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			private static final long serialVersionUID = 5515884441510882460L;

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
		private static final long serialVersionUID = -635451814876234147L;

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
					if (node.isNodeType(SlcTypes.SLC_ARTIFACT)) {
						DistWkspEditorInput dwip = (DistWkspEditorInput) formEditor
								.getEditorInput();
						Map<String, String> params = new HashMap<String, String>();
						params.put(OpenModuleEditor.PARAM_REPO_NODE_PATH,
								dwip.getRepoNodePath());
						params.put(OpenModuleEditor.PARAM_REPO_URI,
								dwip.getUri());
						params.put(OpenModuleEditor.PARAM_WORKSPACE_NAME,
								dwip.getWorkspaceName());
						String path = node.getPath();
						params.put(OpenModuleEditor.PARAM_MODULE_PATH, path);
						CommandUtils.callCommand(OpenModuleEditor.ID, params);
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