/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.jcr.DefaultNodeLabelProvider;
import org.argeo.eclipse.ui.jcr.NodeElementComparer;
import org.argeo.eclipse.ui.jcr.SimpleNodeContentProvider;
import org.argeo.eclipse.ui.specific.EclipseUiSpecificUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.editors.ProcessEditor;
import org.argeo.slc.client.ui.editors.ProcessEditorInput;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** JCR based view of the execution modules. */
public class JcrExecutionModulesView extends ViewPart implements SlcTypes,
		SlcNames {
	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesView.class);

	public static final String ID = "org.argeo.slc.client.ui.jcrExecutionModulesView";

	private TreeViewer viewer;

	private Session session;

	private ExecutionModulesManager modulesManager;

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		EclipseUiSpecificUtils.enableToolTipSupport(viewer);

		ViewContentProvider contentProvider = new ViewContentProvider(session);
		viewer.setContentProvider(contentProvider);
		viewer.setComparer(new NodeElementComparer());
		final ViewLabelProvider viewLabelProvider = new ViewLabelProvider();
		viewer.setLabelProvider(viewLabelProvider);
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());
		getViewSite().setSelectionProvider(viewer);

		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		// Transfer[] tt = new Transfer[] { EditorInputTransfer.getInstance() };
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations, tt, new ViewDragListener());

		try {
			String[] nodeTypes = { SlcTypes.SLC_AGENT,
					SlcTypes.SLC_AGENT_FACTORY, SlcTypes.SLC_EXECUTION_MODULE };
			session.getWorkspace()
					.getObservationManager()
					.addEventListener(
							new VmAgentObserver(viewer.getTree().getDisplay()),
							Event.NODE_ADDED | Event.NODE_REMOVED
									| Event.NODE_MOVED,
							SlcJcrConstants.VM_AGENT_FACTORY_PATH, true, null,
							nodeTypes, false);
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

		@Override
		protected Object[] sort(Object parent, Object[] children) {
			Object[] sorted = new Object[children.length];
			System.arraycopy(children, 0, sorted, 0, children.length);
			Arrays.sort(sorted, new ViewComparator());
			return sorted;
		}

		@Override
		protected List<Node> filterChildren(List<Node> children)
				throws RepositoryException {
			for (Iterator<Node> it = children.iterator(); it.hasNext();) {
				Node node = it.next();
				// execution spec definitions
				if (node.getName().equals(SLC_EXECUTION_SPECS))
					it.remove();
				// flow values
				else if (node.getParent().isNodeType(
						SlcTypes.SLC_EXECUTION_FLOW))
					it.remove();
			}
			return super.filterChildren(children);
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Node) {
				Node node = (Node) element;
				try {
					if (node.isNodeType(SlcTypes.SLC_EXECUTION_FLOW))
						return false;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot check has children", e);
				}
			}
			return super.hasChildren(element);
		}
	}

	static class ViewComparator implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			try {
				if (o1 instanceof Node && o2 instanceof Node) {
					Node node1 = (Node) o1;
					Node node2 = (Node) o2;

					if (node1.getName().equals(SLC_EXECUTION_SPECS))
						return -100;
					if (node2.getName().equals(SLC_EXECUTION_SPECS))
						return 100;

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

	class VmAgentObserver extends AsyncUiEventListener {

		public VmAgentObserver(Display display) {
			super(display);
		}

		protected void onEventInUiThread(List<Event> events) {
			for (Event event : events) {
				try {
					String path = event.getPath();

					if (session.itemExists(path)) {
						Node parentNode = session.getNode(path);// .getParent();
						if (log.isTraceEnabled())
							log.trace("Refresh " + parentNode + " after event "
									+ event);
						viewer.refresh(parentNode);
					}
				} catch (RepositoryException e) {
					log.warn("Cannot process event " + event + ": " + e);
				}
			}

			// TODO: optimize based on event
			viewer.refresh();
		}
	}

	class ViewLabelProvider extends DefaultNodeLabelProvider implements
			ITableLabelProvider {

		@Override
		protected String getText(Node node) throws RepositoryException {
			if (node.isNodeType(NodeType.MIX_TITLE)
					&& node.hasProperty(Property.JCR_TITLE))
				return node.getProperty(Property.JCR_TITLE).getString();
			else if (node.getName().equals(SLC_EXECUTION_SPECS))
				return "Execution Specifications";
			else if (node.getPath().equals(
					SlcJcrConstants.VM_AGENT_FACTORY_PATH))
				return "Internal Agents";
			return super.getText(node);
		}

		@Override
		public Image getImage(Node node) throws RepositoryException {
			// we try to optimize a bit by putting deeper nodes first
			if (node.getParent().isNodeType(
					SlcTypes.SLC_EXECUTION_SPEC_ATTRIBUTE))
				return SlcImages.CHOICES;
			else if (node.isNodeType(SlcTypes.SLC_EXECUTION_SPEC_ATTRIBUTE))
				return SlcImages.EXECUTION_SPEC_ATTRIBUTE;
			else if (node.isNodeType(SlcTypes.SLC_EXECUTION_SPEC))
				return SlcImages.EXECUTION_SPEC;
			else if (node.getName().equals(SLC_EXECUTION_SPECS))
				return SlcImages.EXECUTION_SPECS;
			else if (node.isNodeType(SlcTypes.SLC_EXECUTION_FLOW))
				return SlcImages.FLOW;
			else if (node.isNodeType(SlcTypes.SLC_MODULE)) {
				if (node.getProperty(SLC_STARTED).getBoolean())
					return SlcImages.MODULE;
				else
					return SlcImages.MODULE_STOPPED;
			} else if (node.isNodeType(SlcTypes.SLC_AGENT))
				return SlcImages.AGENT;
			else if (node.isNodeType(SlcTypes.SLC_AGENT_FACTORY))
				return SlcImages.AGENT_FACTORY;
			else
				return SlcImages.FOLDER;
		}

		public String getToolTipText(Node node) throws RepositoryException {
			if (node.isNodeType(NodeType.MIX_TITLE)
					&& node.hasProperty(Property.JCR_DESCRIPTION))
				return node.getProperty(Property.JCR_DESCRIPTION).getString();
			return super.getToolTipText(node);
		}

		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.isNodeType(SLC_EXECUTION_MODULE)) {
						ClientUiPlugin.startStopExecutionModule(modulesManager,
								node);
					} else {
						String path = node.getPath();
						// TODO factorize with editor
						QueryManager qm = node.getSession().getWorkspace()
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

						IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						activePage.openEditor(new ProcessEditorInput(
								new ArrayList<String>(paths), true),
								ProcessEditor.ID);
					}
				}
			} catch (Exception e) {
				throw new SlcException("Cannot open " + obj, e);
			}
		}

	}

	/** Listen to drags */
	class ViewDragListener extends DragSourceAdapter {
		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof Node) {
				Node node = (Node) selection.getFirstElement();
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					try {
						event.data = node.getPath();
					} catch (RepositoryException e) {
						throw new SlcException("Cannot read node", e);
					}
				}
			}
		}
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}