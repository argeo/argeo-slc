package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.jcr.DefaultNodeLabelProvider;
import org.argeo.eclipse.ui.jcr.SimpleNodeContentProvider;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.editors.ProcessEditor;
import org.argeo.slc.client.ui.editors.ProcessEditorInput;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** JCR based view of the execution modules. */
public class JcrExecutionModulesView extends ViewPart implements SlcTypes,
		SlcNames {
	// private final static Log log = LogFactory
	// .getLog(JcrExecutionModulesView.class);

	public static final String ID = "org.argeo.slc.client.ui.jcrExecutionModulesView";

	private TreeViewer viewer;

	private Session session;

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		ColumnViewerToolTipSupport.enableFor(viewer);

		ViewContentProvider contentProvider = new ViewContentProvider(session);

		viewer.setContentProvider(contentProvider);
		final ViewLabelProvider viewLabelProvider = new ViewLabelProvider();
		viewer.setLabelProvider(viewLabelProvider);
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		// Transfer[] tt = new Transfer[] { EditorInputTransfer.getInstance() };
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations, tt, new ViewDragListener());

		try {
			session.getWorkspace()
					.getObservationManager()
					.addEventListener(
							new VmAgentObserver(),
							Event.NODE_ADDED | Event.NODE_REMOVED
									| Event.NODE_MOVED,
							SlcJcrConstants.VM_AGENT_FACTORY_PATH, true, null,
							null, false);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add observer", e);
		}
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void refreshView() {
		viewer.setInput(getViewSite());
	}

	class ViewContentProvider extends SimpleNodeContentProvider {

		public ViewContentProvider(Session session) {
			super(session,
					new String[] { SlcJcrConstants.VM_AGENT_FACTORY_PATH });
		}

//		@Override
//		protected Object[] getChildren(Node node) throws RepositoryException {
//			if (node.isNodeType(SlcTypes.SLC_AGENT_FACTORY)) {
//				List<AgentNodesWrapper> wrappers = new ArrayList<AgentNodesWrapper>();
//				for (NodeIterator nit = node.getNodes(); nit.hasNext();) {
//					wrappers.add(new AgentNodesWrapper(nit.nextNode()));
//				}
//				return wrappers.toArray();
//			}
//			return super.getChildren(node);
//		}

		@Override
		protected Object[] sort(Object parent, Object[] children) {
			Object[] sorted = new Object[children.length];
			System.arraycopy(children, 0, sorted, 0, children.length);
			Arrays.sort(sorted, new ViewComparator());
			return sorted;
		}
	}

	static class ViewComparator implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			try {
				if (o1 instanceof Node && o2 instanceof Node) {
					Node node1 = (Node) o1;
					Node node2 = (Node) o2;
					if (node1.isNodeType(SLC_EXECUTION_FLOW)
							&& node2.isNodeType(SLC_EXECUTION_FLOW)) {
						return node1.getName().compareTo(node2.getName());
					} else if (node1.isNodeType(SLC_EXECUTION_FLOW)
							&& !node2.isNodeType(SLC_EXECUTION_FLOW)) {
						return 1;
					} else if (!node1.isNodeType(SLC_EXECUTION_FLOW)
							&& node2.isNodeType(SLC_EXECUTION_FLOW)) {
						return -1;
					} else {
						// TODO: check title
						return node1.getName().compareTo(node2.getName());
					}
				}
			} catch (RepositoryException e) {
				throw new ArgeoException("Cannot compare " + o1 + " and " + o2,
						e);
			}
			return 0;
		}

	}

//	/** Wraps the execution modules of an agent. */
//	static class AgentNodesWrapper extends NodesWrapper {
//
//		public AgentNodesWrapper(Node node) {
//			super(node);
//		}
//
//		protected List<WrappedNode> getWrappedNodes()
//				throws RepositoryException {
//			List<WrappedNode> children = new ArrayList<WrappedNode>();
//			Node executionModules = getNode();
//			for (NodeIterator nit = executionModules.getNodes(); nit.hasNext();) {
//				for (NodeIterator nitVersions = nit.nextNode().getNodes(); nitVersions
//						.hasNext();) {
//					children.add(new WrappedNode(this, nitVersions.nextNode()));
//				}
//			}
//			return children;
//		}
//
//	}

	class VmAgentObserver extends AsyncUiEventListener {
		protected void onEventInUiThread(EventIterator events) {
			viewer.refresh();
		}
	}

	class ViewLabelProvider extends DefaultNodeLabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Node node) throws RepositoryException {
			if (node.isNodeType(SlcTypes.SLC_AGENT))
				return SlcImages.AGENT;
			else if (node.isNodeType(SlcTypes.SLC_MODULE))
				return SlcImages.MODULE;
			else if (node.isNodeType(SlcTypes.SLC_EXECUTION_FLOW))
				return SlcImages.FLOW;
			else
				return SlcImages.FOLDER;
		}

		public String getToolTipText(Node node) throws RepositoryException {
			if (node.isNodeType(SlcTypes.SLC_MODULE)
					&& node.hasProperty(Property.JCR_DESCRIPTION))
				return node.getProperty(Property.JCR_DESCRIPTION).getString();
			return super.getToolTipText(node);
		}

	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.isNodeType(SLC_EXECUTION_FLOW)) {
						List<String> paths = new ArrayList<String>();
						paths.add(node.getPath());
						IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						activePage.openEditor(new ProcessEditorInput(paths,
								true), ProcessEditor.ID);
					}
				}
			} catch (Exception e) {
				throw new SlcException("Cannot open " + obj, e);
			}
		}

	}

	class ViewDragListener extends DragSourceAdapter {
		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof Node) {
				Node node = (Node) selection.getFirstElement();
				// try {
				// if (node.isNodeType(SLC_EXECUTION_FLOW)) {
				// if (EditorInputTransfer.getInstance().isSupportedType(
				// event.dataType)) {
				// ProcessEditorInput pei = new ProcessEditorInput(
				// node.getPath());
				// EditorInputData eid = EditorInputTransfer
				// .createEditorInputData(ProcessEditor.ID,
				// pei);
				// event.data = new EditorInputTransfer.EditorInputData[] { eid
				// };
				//
				// }
				// }
				// } catch (RepositoryException e1) {
				// throw new SlcException("Cannot drag " + node, e1);
				// }

				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					try {
						event.data = node.getPath();
					} catch (RepositoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void setSession(Session session) {
		this.session = session;
	}

}