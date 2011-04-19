package org.argeo.slc.client.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.process.RealizedFlow;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ProcessBuilderPage extends FormPage implements SlcNames, SlcTypes {
	public final static String ID = "processBuilderPage";
	//private final static Log log = LogFactory.getLog(ProcessBuilderPage.class);

	private Node processNode;

	private TreeViewer flowsViewer;
	private Label status;

	private AbstractFormPart formPart;
	private StatusObserver statusObserver;

	public ProcessBuilderPage(ProcessEditor editor, Node processNode) {
		super(editor, ID, "Definition");
		this.processNode = processNode;

	}

	@Override
	protected void createFormContent(IManagedForm mf) {
		try {
			ScrolledForm form = mf.getForm();
			form.setText("Process " + processNode.getName());
			GridLayout mainLayout = new GridLayout(1, true);
			form.getBody().setLayout(mainLayout);

			createControls(form.getBody());
			createBuilder(form.getBody());

			// form
			formPart = new AbstractFormPart() {

			};
			getManagedForm().addPart(formPart);
			if (getProcessStatus().equals(ExecutionProcess.UNINITIALIZED))
				formPart.markDirty();

			// observation
			statusObserver = new StatusObserver();
			ObservationManager observationManager = processNode.getSession()
					.getWorkspace().getObservationManager();
			observationManager.addEventListener(statusObserver,
					Event.PROPERTY_CHANGED, processNode.getPath(), true, null,
					null, false);

		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot create form content", e);
		}
	}

	protected void createControls(Composite parent) {
		FormToolkit tk = getManagedForm().getToolkit();

		Composite controls = tk.createComposite(parent);
		controls.setLayout(new RowLayout());
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Button run = tk.createButton(controls, null, SWT.PUSH);
		run.setToolTipText("Run");
		run.setImage(SlcImages.LAUNCH);
		run.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((ProcessEditor) getEditor()).process();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button remove = tk.createButton(controls, null, SWT.PUSH);
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

		Button clear = tk.createButton(controls, null, SWT.PUSH);
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

		status = tk.createLabel(controls, getProcessStatus());
	}

	protected String getProcessStatus() {
		try {
			return processNode.getProperty(SLC_STATUS).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot retrieve status for " + processNode,
					e);
		}
	}

	protected void createBuilder(Composite parent) {
		FormToolkit tk = getManagedForm().getToolkit();
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth(4);
		sashForm.setLayout(new FillLayout());
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite top = tk.createComposite(sashForm);
		GridLayout gl = new GridLayout(1, false);
		top.setLayout(gl);

		flowsViewer = new TreeViewer(top);
		flowsViewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		flowsViewer.setLabelProvider(new ViewLabelProvider());
		flowsViewer.setContentProvider(new ViewContentProvider());
		flowsViewer.addSelectionChangedListener(new SelectionChangedListener());

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		flowsViewer.addDropSupport(operations, tt, new ViewDropListener(
				flowsViewer));

		flowsViewer.setInput(getEditorSite());
		flowsViewer.setInput(processNode);

		Composite bottom = tk.createComposite(sashForm);
		bottom.setLayout(new GridLayout(1, false));
		sashForm.setWeights(getWeights());
	}

	protected int[] getWeights() {
		return new int[] { 70, 30 };
	}

	/*
	 * CONTROLLERS
	 */
	protected void addFlow(String path) {
		try {
			Node flowNode = processNode.getNode(SLC_FLOW).addNode(SLC_FLOW);
			flowNode.addMixin(SLC_REALIZED_FLOW);
			Node address = flowNode.addNode(SLC_ADDRESS, NodeType.NT_ADDRESS);
			address.setProperty(Property.JCR_PATH, path);
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
		formPart.commit(onSave);
	}

	@Override
	public void setFocus() {
		flowsViewer.getTree().setFocus();
	}

	@Override
	public void dispose() {
		JcrUtils.unregisterQuietly(processNode, statusObserver);
		super.dispose();
	}

	// Specific Providers for the current view.
	protected class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

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

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

	}

	protected class ViewLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			Node node = (Node) element;
			try {
				if (node.isNodeType(SLC_REALIZED_FLOW)) {
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
				if (node.isNodeType(SLC_REALIZED_FLOW)) {
					return SlcImages.FLOW;
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot display " + element, e);
			}
			return super.getImage(element);
		}

	}

	// Parameter view is updated each time a new line is selected
	class SelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent evt) {

			IStructuredSelection curSelection = (IStructuredSelection) evt
					.getSelection();
			Object obj = curSelection.getFirstElement();

			if (obj instanceof RealizedFlow) {
				// RealizedFlow rf = (RealizedFlow) obj;
				// curSelectedRow = realizedFlows.indexOf(rf);
				// refreshParameterview();
				// setFocus();
			}
		}
	}

	protected class ViewDropListener extends ViewerDropAdapter {

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			String path = data.toString();
			addFlow(path);
			return true;
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			return true;
		}
	}

	class StatusObserver implements EventListener {

		public void onEvent(EventIterator events) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					status.setText(getProcessStatus());
				}
			});
			// flowsViewer.refresh();
		}

	}
}
