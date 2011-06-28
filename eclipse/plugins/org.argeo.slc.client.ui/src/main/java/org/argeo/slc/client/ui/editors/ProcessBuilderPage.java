package org.argeo.slc.client.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ProcessBuilderPage extends FormPage implements SlcNames {
	public final static String ID = "processBuilderPage";
	// private final static Log log =
	// LogFactory.getLog(ProcessBuilderPage.class);

	private Node processNode;

	private TreeViewer flowsViewer;
	private TableViewer valuesViewer;
	private Label statusLabel;
	private Button run;
	private Button remove;
	private Button clear;

	private AbstractFormPart formPart;
	private EventListener statusObserver;

	public ProcessBuilderPage(ProcessEditor editor, Node processNode) {
		super(editor, ID, "Definition");
		this.processNode = processNode;
	}

	@Override
	protected void createFormContent(IManagedForm mf) {
		try {
			ScrolledForm form = mf.getForm();
			form.setExpandHorizontal(true);
			form.setExpandVertical(true);
			form.setText("Process " + processNode.getName());
			GridLayout mainLayout = new GridLayout(1, true);
			form.getBody().setLayout(mainLayout);

			createControls(form.getBody());
			createBuilder(form.getBody());

			// form
			formPart = new AbstractFormPart() {

			};
			getManagedForm().addPart(formPart);

			// observation
			statusObserver = new AsyncUiEventListener(form.getDisplay()) {
				protected void onEventInUiThread(List<Event> events) {
					statusChanged();
				}
			};
			ObservationManager observationManager = processNode.getSession()
					.getWorkspace().getObservationManager();
			observationManager.addEventListener(statusObserver,
					Event.PROPERTY_CHANGED, processNode.getPath(), true, null,
					null, false);

			// add initial flows
			addInitialFlows();

		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot create form content", e);
		}
	}

	protected void createControls(Composite parent) {
		FormToolkit tk = getManagedForm().getToolkit();

		Composite controls = tk.createComposite(parent);
		controls.setLayout(new RowLayout());
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		run = tk.createButton(controls, null, SWT.PUSH);
		run.setToolTipText("Run");
		run.setImage(SlcImages.LAUNCH);
		run.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (isFinished(getProcessStatus())) {
					relaunch();
				} else {
					((ProcessEditor) getEditor()).process();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		remove = tk.createButton(controls, null, SWT.PUSH);
		remove.setImage(SlcImages.REMOVE_ONE);
		remove.setToolTipText("Remove selected flows");
		remove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				removeSelectedFlows();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		clear = tk.createButton(controls, null, SWT.PUSH);
		clear.setImage(SlcImages.REMOVE_ALL);
		clear.setToolTipText("Clear all flows");
		clear.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				removeAllFlows();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Composite statusComposite = tk.createComposite(controls);
		RowData rowData = new RowData();
		rowData.width = 100;
		rowData.height = 16;
		statusComposite.setLayoutData(rowData);
		statusComposite.setLayout(new FillLayout());
		statusLabel = tk.createLabel(statusComposite, getProcessStatus());

		// make sure all controls are in line with status
		statusChanged();
	}

	protected void createBuilder(Composite parent) {
		FormToolkit tk = getManagedForm().getToolkit();
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth(4);
		GridData sahFormGd = new GridData(SWT.FILL, SWT.FILL, true, true);
		sahFormGd.widthHint = 400;
		sashForm.setLayoutData(sahFormGd);

		Composite flowsComposite = tk.createComposite(sashForm);
		flowsComposite.setLayout(new GridLayout(1, false));

		flowsViewer = new TreeViewer(flowsComposite);
		flowsViewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		flowsViewer.setLabelProvider(new FlowsLabelProvider());
		flowsViewer.setContentProvider(new FlowsContentProvider());
		flowsViewer.addSelectionChangedListener(new FlowsSelectionListener());

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		flowsViewer.addDropSupport(operations, tt, new FlowsDropListener(
				flowsViewer));

		flowsViewer.setInput(getEditorSite());
		flowsViewer.setInput(processNode);

		Composite valuesComposite = tk.createComposite(sashForm);
		valuesComposite.setLayout(new GridLayout(1, false));

		valuesViewer = new TableViewer(valuesComposite);
		GridData valuedGd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// valuedGd.widthHint = 200;
		valuesViewer.getTable().setLayoutData(valuedGd);
		valuesViewer.setContentProvider(new ValuesContentProvider());
		initializeValuesViewer(valuesViewer);
		sashForm.setWeights(getWeights());
		valuesViewer.setInput(getEditorSite());
	}

	/** Creates the columns of the values viewer */
	protected void initializeValuesViewer(TableViewer viewer) {
		String[] titles = { "Name", "Value" };
		int[] bounds = { 200, 100 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			if (i == 0) {
				column.setLabelProvider(new ColumnLabelProvider() {
					public String getText(Object element) {
						try {
							Node specAttrNode = (Node) element;
							return specAttrNode.getName();
						} catch (RepositoryException e) {
							throw new SlcException("Cannot get value", e);
						}
					}
				});
			} else if (i == 1) {
				column.setLabelProvider(new ColumnLabelProvider() {
					public String getText(Object element) {
						Object obj = getAttributeSpecValue((Node) element);
						return obj != null ? obj.toString() : "";
					}
				});
				column.setEditingSupport(new ValuesEditingSupport(viewer));
			}

		}
		Table table = viewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
	}

	protected int[] getWeights() {
		return new int[] { 50, 50 };
	}

	/*
	 * CONTROLLERS
	 */
	/** Opens a new editor with a copy of this process */
	protected void relaunch() {
		try {
			Node duplicatedNode = duplicateProcess();
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			activePage.openEditor(
					new ProcessEditorInput(duplicatedNode.getPath()),
					ProcessEditor.ID);
			getEditor().close(false);
		} catch (Exception e1) {
			throw new SlcException("Cannot relaunch " + processNode, e1);
		}
	}

	/** Duplicates the process */
	protected Node duplicateProcess() {
		try {
			Session session = processNode.getSession();
			String uuid = UUID.randomUUID().toString();
			String destPath = SlcJcrUtils.createExecutionProcessPath(uuid);
			Node newNode = JcrUtils.mkdirs(session, destPath,
					SlcTypes.SLC_PROCESS);
			JcrUtils.copy(processNode, newNode);
			// session.getWorkspace().copy(processNode.getPath(), destPath);
			// Node newNode = session.getNode(destPath);
			// make sure that we kept the mixins
			// newNode.addMixin(NodeType.MIX_CREATED);
			// newNode.addMixin(NodeType.MIX_LAST_MODIFIED);
			newNode.setProperty(SLC_UUID, uuid);
			newNode.setProperty(SLC_STATUS, ExecutionProcess.INITIALIZED);
			session.save();
			return newNode;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot duplicate process", e);
		}
	}

	/** Reflects a status change */
	protected void statusChanged() {
		String status = getProcessStatus();
		statusLabel.setText(status);
		Boolean isEditable = isEditable(status);
		run.setEnabled(isEditable);
		remove.setEnabled(isEditable);
		clear.setEnabled(isEditable);
		// flowsViewer.getTree().setEnabled(isEditable);
		if (status.equals(ExecutionProcess.COMPLETED)
				|| status.equals(ExecutionProcess.ERROR)) {
			run.setEnabled(true);
			run.setImage(SlcImages.RELAUNCH);
			run.setToolTipText("Relaunch");
		}
	}

	/** Adds initial flows from the editor input if any */
	protected void addInitialFlows() {
		for (String path : ((ProcessEditorInput) getEditorInput())
				.getInitialFlowPaths()) {
			addFlow(path);
		}
	}

	/**
	 * Adds a new flow.
	 * 
	 * @param path
	 *            the path of the flow
	 */
	protected void addFlow(String path) {
		try {
			Node flowNode = processNode.getSession().getNode(path);
			Node realizedFlowNode = processNode.getNode(SLC_FLOW).addNode(
					SLC_FLOW);
			realizedFlowNode.addMixin(SlcTypes.SLC_REALIZED_FLOW);
			Node address = realizedFlowNode.addNode(SLC_ADDRESS,
					NodeType.NT_ADDRESS);
			address.setProperty(Property.JCR_PATH, path);

			// copy spec attributes
			Node specAttrsBase;
			if (flowNode.hasProperty(SLC_SPEC)) {
				Node executionSpecNode = flowNode.getProperty(SLC_SPEC)
						.getNode();
				specAttrsBase = executionSpecNode;
				String executionSpecName = executionSpecNode.getProperty(
						SLC_NAME).getString();
				realizedFlowNode.setProperty(SLC_SPEC, executionSpecName);
			} else
				specAttrsBase = flowNode;

			specAttrs: for (NodeIterator nit = specAttrsBase.getNodes(); nit
					.hasNext();) {
				Node specAttrNode = nit.nextNode();
				String attrName = specAttrNode.getName();
				if (!specAttrNode
						.isNodeType(SlcTypes.SLC_EXECUTION_SPEC_ATTRIBUTE))
					continue specAttrs;
				Node realizedAttrNode = realizedFlowNode.addNode(specAttrNode
						.getName());
				JcrUtils.copy(specAttrNode, realizedAttrNode);

				// ovveride with flow value
				if (flowNode.hasNode(attrName)) {
					// assuming this is a primitive
					realizedAttrNode.setProperty(SLC_VALUE,
							flowNode.getNode(attrName).getProperty(SLC_VALUE)
									.getValue());
				}
			}

			flowsViewer.refresh();
			formPart.markDirty();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot drop " + path, e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void removeSelectedFlows() {
		if (!flowsViewer.getSelection().isEmpty()) {
			Iterator<Object> it = ((StructuredSelection) flowsViewer
					.getSelection()).iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				try {
					node.remove();
				} catch (RepositoryException e) {
					throw new ArgeoException("Cannot remove " + node, e);
				}
			}
			flowsViewer.refresh();
			formPart.markDirty();
		}
	}

	protected void removeAllFlows() {
		try {
			for (NodeIterator nit = processNode.getNode(SLC_FLOW).getNodes(); nit
					.hasNext();) {
				nit.nextNode().remove();
			}
			flowsViewer.refresh();
			formPart.markDirty();
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot remove flows from " + processNode,
					e);
		}
	}

	public void commit(Boolean onSave) {
		if (onSave)
			statusLabel.setText(getProcessStatus());
		formPart.commit(onSave);
	}

	/*
	 * STATE
	 */
	protected String getProcessStatus() {
		try {
			return processNode.getProperty(SLC_STATUS).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot retrieve status for " + processNode,
					e);
		}
	}

	/** Optimization so that we don't call the node each time */
	protected Boolean isEditable(String status) {
		return status.equals(ExecutionProcess.NEW)
				|| status.equals(ExecutionProcess.INITIALIZED);
	}

	protected Boolean isFinished(String status) {
		return status.equals(ExecutionProcess.COMPLETED)
				|| status.equals(ExecutionProcess.ERROR);
	}

	/*
	 * LIFECYCLE
	 */
	@Override
	public void dispose() {
		JcrUtils.unregisterQuietly(processNode, statusObserver);
		super.dispose();
	}

	/*
	 * UTILITIES
	 */
	protected static Object getAttributeSpecValue(Node specAttrNode) {
		try {
			if (specAttrNode.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE)) {
				if (!specAttrNode.hasProperty(SLC_VALUE))
					return null;
				String type = specAttrNode.getProperty(SLC_TYPE).getString();
				// TODO optimize based on data type?
				Object value = PrimitiveUtils.convert(type, specAttrNode
						.getProperty(SLC_VALUE).getString());
				// log.debug(specAttrNode + ", type=" + type + ", value=" +
				// value);
				return value;
			}
			return null;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get value", e);
		}

	}

	/*
	 * FLOWS SUBCLASSES
	 */
	static class FlowsContentProvider implements ITreeContentProvider {
		public Object[] getElements(Object obj) {
			if (!(obj instanceof Node))
				return new Object[0];

			try {
				Node node = (Node) obj;
				List<Node> children = new ArrayList<Node>();
				for (NodeIterator nit = node.getNode(SLC_FLOW).getNodes(); nit
						.hasNext();)
					children.add(nit.nextNode());
				return children.toArray();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot list children of " + obj, e);
			}
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		public Object[] getChildren(Object parentElement) {
			// no children for the time being
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

	}

	static class FlowsLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			Node node = (Node) element;
			try {
				if (node.isNodeType(SlcTypes.SLC_REALIZED_FLOW)) {
					if (node.hasNode(SLC_ADDRESS)) {
						String path = node.getNode(SLC_ADDRESS)
								.getProperty(Property.JCR_PATH).getString();
						return SlcJcrUtils.flowExecutionModuleName(path) + ":"
								+ SlcJcrUtils.flowRelativePath(path);
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot display " + element, e);
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			Node node = (Node) element;
			try {
				if (node.isNodeType(SlcTypes.SLC_REALIZED_FLOW)) {
					return SlcImages.FLOW;
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot display " + element, e);
			}
			return super.getImage(element);
		}

	}

	/** Parameter view is updated each time a new line is selected */
	class FlowsSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent evt) {
			if (evt.getSelection().isEmpty()) {
				valuesViewer.setInput(getEditorSite());
				return;
			}
			Node realizedFlowNode = (Node) ((IStructuredSelection) evt
					.getSelection()).getFirstElement();
			valuesViewer.setInput(realizedFlowNode);
		}
	}

	/** Manages drop event. */
	class FlowsDropListener extends ViewerDropAdapter {

		public FlowsDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			String path = data.toString();
			try {
				// either a node or a whole directory was dragged
				QueryManager qm = processNode.getSession().getWorkspace()
						.getQueryManager();
				String statement = "SELECT * FROM ["
						+ SlcTypes.SLC_EXECUTION_FLOW
						+ "] WHERE ISDESCENDANTNODE(['" + path
						+ "']) OR ISSAMENODE(['" + path + "'])";
				// log.debug(statement);
				Query query = qm.createQuery(statement, Query.JCR_SQL2);

				// order paths
				SortedSet<String> paths = new TreeSet<String>();
				for (NodeIterator nit = query.execute().getNodes(); nit
						.hasNext();) {
					paths.add(nit.nextNode().getPath());
				}

				for (String p : paths) {
					addFlow(p);
				}
				return true;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot query flows under " + path, e);
			}
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			return isEditable(getProcessStatus());
		}
	}

	/*
	 * VALUES SUBCLASSES
	 */
	static class ValuesContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof Node))
				return new Object[0];

			try {
				Node realizedFlowNode = (Node) inputElement;
				List<Node> specAttributes = new ArrayList<Node>();
				specAttrs: for (NodeIterator nit = realizedFlowNode.getNodes(); nit
						.hasNext();) {
					Node specAttrNode = nit.nextNode();
					if (!specAttrNode
							.isNodeType(SlcTypes.SLC_EXECUTION_SPEC_ATTRIBUTE))
						continue specAttrs;
					specAttributes.add(specAttrNode);
				}
				return specAttributes.toArray();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get elements", e);
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class ValuesEditingSupport extends EditingSupport {
		private final TableViewer tableViewer;

		public ValuesEditingSupport(ColumnViewer viewer) {
			super(viewer);
			tableViewer = (TableViewer) viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			try {
				Node specAttrNode = (Node) element;
				if (specAttrNode
						.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE))
					return new TextCellEditor(tableViewer.getTable());
				return null;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get celle editor", e);
			}
		}

		@Override
		protected boolean canEdit(Object element) {
			try {
				Node specAttrNode = (Node) element;
				return !(specAttrNode.getProperty(SLC_IS_IMMUTABLE)
						.getBoolean() || specAttrNode.getProperty(
						SLC_IS_CONSTANT).getBoolean())
						&& specAttrNode
								.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE);
			} catch (RepositoryException e) {
				throw new SlcException("Cannot check canEdit", e);
			}
		}

		@Override
		protected Object getValue(Object element) {
			Node specAttrNode = (Node) element;
			try {
				Object value = getAttributeSpecValue(specAttrNode);
				if (value == null)
					throw new SlcException("Unsupported attribute " + element);
				if (specAttrNode
						.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE))
					return value.toString();
				return value;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get value for " + element, e);
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			try {
				Node specAttrNode = (Node) element;
				if (specAttrNode
						.isNodeType(SlcTypes.SLC_PRIMITIVE_SPEC_ATTRIBUTE)) {
					String type = specAttrNode.getProperty(SLC_TYPE)
							.getString();
					SlcJcrUtils.setPrimitiveAsProperty(specAttrNode, SLC_VALUE,
							type, value);
					valuesViewer.refresh();
					formPart.markDirty();
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get celle editor", e);
			}
		}

	}
}
