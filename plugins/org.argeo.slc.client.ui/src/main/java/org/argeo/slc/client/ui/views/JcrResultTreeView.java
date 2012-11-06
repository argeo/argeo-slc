package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcUiConstants;
import org.argeo.slc.client.ui.commands.AddResultFolder;
import org.argeo.slc.client.ui.editors.ProcessEditor;
import org.argeo.slc.client.ui.editors.ProcessEditorInput;
import org.argeo.slc.client.ui.model.ParentNodeFolder;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.ResultParentUtils;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.argeo.slc.client.ui.model.VirtualFolder;
import org.argeo.slc.client.ui.providers.ResultTreeContentProvider;
import org.argeo.slc.client.ui.providers.ResultTreeLabelProvider;
import org.argeo.slc.jcr.SlcJcrResultUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** SLC generic JCR Result tree view. */
public class JcrResultTreeView extends ViewPart {
	public final static String ID = ClientUiPlugin.ID + ".jcrResultTreeView";

	// private final static Log log =
	// LogFactory.getLog(JcrResultTreeView.class);

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer resultTreeViewer;
	private TableViewer propertiesViewer;

	private EventListener resultsObserver = null;

	private final static String[] observedNodeTypes = {
			SlcTypes.SLC_TEST_RESULT, SlcTypes.SLC_RESULT_FOLDER,
			NodeType.NT_UNSTRUCTURED };

	// FIXME cache to ease refresh after D&D
	private ResultParent lastSelectedTargetElement;
	private ResultParent lastSelectedTargetElementParent;
	private ResultParent lastSelectedSourceElementParent;

	/**
	 * To be overridden to adapt size of form and result frames.
	 */
	protected int[] getWeights() {
		return new int[] { 70, 30 };
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		// Main layout
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setSashWidth(4);
		sashForm.setLayout(new FillLayout());

		// Create the tree on top of the view
		Composite top = new Composite(sashForm, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		top.setLayout(gl);
		resultTreeViewer = createResultsTreeViewer(top);

		// Create the property viewer on the bottom
		Composite bottom = new Composite(sashForm, SWT.NONE);
		bottom.setLayout(new GridLayout(1, false));
		propertiesViewer = createPropertiesViewer(bottom);

		sashForm.setWeights(getWeights());

		// Refresh the view to initialize it
		refresh(null);
	}

	// The main tree viewer
	protected TreeViewer createResultsTreeViewer(Composite parent) {
		int style = SWT.BORDER | SWT.MULTI;

		TreeViewer viewer = new TreeViewer(parent, style);
		viewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new ResultTreeContentProvider());

		// Add label provider with label decorator
		ResultTreeLabelProvider rtLblProvider = new ResultTreeLabelProvider();
		ILabelDecorator decorator = ClientUiPlugin.getDefault().getWorkbench()
				.getDecoratorManager().getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(rtLblProvider,
				decorator));
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

		// viewer.setLabelProvider(rtLblProvider);
		getSite().setSelectionProvider(viewer);

		// add drag & drop support
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(operations, tt, new ViewDragListener());
		viewer.addDropSupport(operations, tt, new ViewDropListener(viewer));

		// add context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		// add change listener to display TestResult information in the property
		// viewer
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					IStructuredSelection sel = (IStructuredSelection) event
							.getSelection();
					Object firstItem = sel.getFirstElement();
					if (firstItem instanceof SingleResultNode)
						propertiesViewer
								.setInput(((SingleResultNode) firstItem)
										.getNode());
					else
						propertiesViewer.setInput(null);
					lastSelectedTargetElement = (ResultParent) firstItem;
					lastSelectedTargetElementParent = (ResultParent) ((ResultParent) firstItem)
							.getParent();
				}
			}
		});
		return viewer;
	}

	// Detailed property viewer
	protected TableViewer createPropertiesViewer(Composite parent) {
		propertiesViewer = new TableViewer(parent);
		propertiesViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		propertiesViewer.getTable().setHeaderVisible(true);
		propertiesViewer.setContentProvider(new PropertiesContentProvider());
		TableViewerColumn col = new TableViewerColumn(propertiesViewer,
				SWT.NONE);
		col.getColumn().setText("Name");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				try {
					return ((Property) element).getName();
				} catch (RepositoryException e) {
					throw new ArgeoException(
							"Unexpected exception in label provider", e);
				}
			}
		});
		col = new TableViewerColumn(propertiesViewer, SWT.NONE);
		col.getColumn().setText("Value");
		col.getColumn().setWidth(400);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				try {
					Property property = (Property) element;
					if (property.getType() == PropertyType.BINARY)
						return "<binary>";
					else if (property.isMultiple()) {
						StringBuffer buf = new StringBuffer("[");
						Value[] values = property.getValues();
						for (int i = 0; i < values.length; i++) {
							if (i != 0)
								buf.append(", ");
							buf.append(values[i].getString());
						}
						buf.append(']');
						return buf.toString();
					} else
						return property.getValue().getString();
				} catch (RepositoryException e) {
					throw new ArgeoException(
							"Unexpected exception in label provider", e);
				}
			}
		});
		col = new TableViewerColumn(propertiesViewer, SWT.NONE);
		col.getColumn().setText("Type");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				try {
					return PropertyType.nameFromValue(((Property) element)
							.getType());
				} catch (RepositoryException e) {
					throw new ArgeoException(
							"Unexpected exception in label provider", e);
				}
			}
		});
		propertiesViewer.setInput(getViewSite());
		return propertiesViewer;
	}

	/**
	 * Override to provide specific behaviour. Typically to enable the display
	 * of a result file.
	 * 
	 * @param evt
	 */
	protected void processDoubleClick(DoubleClickEvent evt) {
		Object obj = ((IStructuredSelection) evt.getSelection())
				.getFirstElement();
		try {
			if (obj instanceof SingleResultNode) {
				SingleResultNode srNode = (SingleResultNode) obj;
				Node node = srNode.getNode();
				// FIXME: open a default result editor
				if (node.isNodeType(SlcTypes.SLC_PROCESS)) {
					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					activePage.openEditor(
							new ProcessEditorInput(node.getPath()),
							ProcessEditor.ID);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot open " + obj, e);
		}
	}

	@Override
	public void setFocus() {
	}

	/**
	 * refreshes the passed node and its corresponding subtree.
	 * 
	 * @param node
	 *            cannot be null
	 * 
	 */
	public boolean jcrRefresh(Node node) {
		boolean isPassed = true;
		try {
			if (node.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
				isPassed = node.getNode(SlcNames.SLC_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean();
			} else if (node.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
				NodeIterator ni = node.getNodes();
				// quicker but wrong : refresh will stop as soon as a failed
				// test is found and the whole tree won't be refreshed
				// while (isPassed && ni.hasNext()){
				while (ni.hasNext()) {
					Node currChild = ni.nextNode();
					isPassed = isPassed & jcrRefresh(currChild);
				}
				if (isPassed != node.getNode(SlcNames.SLC_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean()) {
					node.getNode(SlcNames.SLC_STATUS).setProperty(
							SlcNames.SLC_SUCCESS, isPassed);
					node.getSession().save();
					return isPassed;
				}
			} else
				; // do nothing
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listeners", e);
		}
		return isPassed;
	}

	/**
	 * refreshes the passed resultParent and its corresponding subtree. It
	 * refreshes the whole viewer if null is passed.
	 * 
	 * @param ResultParent
	 * 
	 */
	public void refresh(ResultParent resultParent) {
		if (resultParent == null) {
			resultTreeViewer.setInput(initializeResultTree());
			if (resultsObserver == null) {
				try {
					ObservationManager observationManager = session
							.getWorkspace().getObservationManager();
					resultsObserver = new ResultObserver(resultTreeViewer
							.getTree().getDisplay());
					observationManager.addEventListener(resultsObserver,
							Event.NODE_ADDED | Event.NODE_REMOVED, UserJcrUtils
									.getUserHome(session).getPath(), true,
							null, observedNodeTypes, false);
				} catch (RepositoryException e) {
					throw new SlcException("Cannot register listeners", e);
				}
			}

		} else {
			// FIXME implement refresh for a specific ResultParent object.
			if (resultParent instanceof ResultFolder) {
				ResultFolder currFolder = (ResultFolder) resultParent;
				jcrRefresh(currFolder.getNode());
				currFolder.forceFullRefresh();
				resultTreeViewer.refresh(lastSelectedTargetElement);
			}
		}
	}

	private ResultParent[] initializeResultTree() {
		try {
			if (session.nodeExists(SlcJcrResultUtils
					.getSlcResultsBasePath(session))) {
				ResultParent[] roots = new ResultParent[5];

				// My results
				roots[0] = new ParentNodeFolder(null,
						SlcJcrResultUtils.getMyResultParentNode(session),
						SlcUiConstants.DEFAULT_MY_RESULTS_FOLDER_LABEL);

				// today
				Calendar cal = Calendar.getInstance();
				String relPath = JcrUtils.dateAsPath(cal);
				List<String> datePathes = new ArrayList<String>();
				datePathes.add(relPath);
				roots[1] = new VirtualFolder(null,
						ResultParentUtils.getResultsForDates(session,
								datePathes), "Today");

				// Yesterday
				cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -1);
				relPath = JcrUtils.dateAsPath(cal);
				datePathes = new ArrayList<String>();
				datePathes.add(relPath);
				roots[2] = new VirtualFolder(null,
						ResultParentUtils.getResultsForDates(session,
								datePathes), "Yesterday");
				// Last 7 days

				cal = Calendar.getInstance();
				datePathes = new ArrayList<String>();

				for (int i = 0; i < 7; i++) {
					cal.add(Calendar.DAY_OF_YEAR, -i);
					relPath = JcrUtils.dateAsPath(cal);
					datePathes.add(relPath);
				}
				roots[3] = new VirtualFolder(null,
						ResultParentUtils.getResultsForDates(session,
								datePathes), "Last 7 days");

				// All results
				Node otherResultsPar = session.getNode(SlcJcrResultUtils
						.getSlcResultsBasePath(session));
				roots[4] = new ParentNodeFolder(null, otherResultsPar,
						"All results");
				return roots;
			} else
				// no test has yet been processed, we leave the viewer blank
				return null;
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while initializing ResultTree.", re);
		}
	}

	// Manage context menu
	/**
	 * Defines the commands that will pop up in the context menu.
	 **/
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = ClientUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();

		// Building conditions
		IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
				.getSelection();
		boolean isMyResultFolder = false;
		if (selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				if (obj instanceof ResultFolder
						&& (((ResultFolder) obj).getNode())
								.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
					isMyResultFolder = true;
				else if (obj instanceof ParentNodeFolder
						&& (((ParentNodeFolder) obj).getNode().getPath()
								.startsWith(SlcJcrResultUtils
										.getMyResultsBasePath(session))))
					isMyResultFolder = true;
			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while building condition for context menu",
						re);
			}
		}
		// Effective Refresh
		CommandUtils.refreshCommand(menuManager, window, AddResultFolder.ID,
				AddResultFolder.DEFAULT_LABEL,
				ClientUiPlugin.getDefault().getWorkbench().getSharedImages()
						.getImageDescriptor(ISharedImages.IMG_OBJ_ADD),
				isMyResultFolder);
	}

	/* INNER CLASSES */
	class ViewDragListener implements DragSourceListener {

		public void dragStart(DragSourceEvent event) {
			// Check if the drag action should start.
			IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
					.getSelection();
			boolean doIt = false;
			// only one node at a time for the time being.
			if (selection.size() == 1) {
				Object obj = selection.getFirstElement();
				if (obj instanceof SingleResultNode) {
					Node tNode = ((SingleResultNode) obj).getNode();
					try {
						// if (tNode.getPrimaryNodeType().isNodeType(
						// SlcTypes.SLC_TEST_RESULT)
						// && (tNode.getPath()
						// .startsWith(SlcJcrResultUtils
						// .getSlcResultsBasePath(session))))
						if (tNode.getPrimaryNodeType().isNodeType(
								SlcTypes.SLC_TEST_RESULT))
							doIt = true;
						lastSelectedSourceElementParent = (ResultParent) ((SingleResultNode) obj)
								.getParent();
					} catch (RepositoryException re) {
						throw new SlcException(
								"unexpected error while validating drag source",
								re);
					}
				}
			}
			event.doit = doIt;
		}

		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
					.getSelection();
			Object obj = selection.getFirstElement();
			if (obj instanceof SingleResultNode) {
				Node first = ((SingleResultNode) obj).getNode();
				try {
					event.data = first.getIdentifier();

				} catch (RepositoryException re) {
					throw new SlcException(
							"unexpected error while setting data", re);
				}
			}
		}

		public void dragFinished(DragSourceEvent event) {
			// implement here tree refresh in case of a move.
		}
	}

	// Implementation of the Drop Listener
	protected class ViewDropListener extends ViewerDropAdapter {

		private Node currParentNode = null;
		private boolean copyNode = true;

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			boolean validDrop = false;
			try {
				// We can only drop under myResults
				Node targetParentNode = null;
				if (target instanceof ResultFolder) {
					targetParentNode = ((ResultFolder) target).getNode();
				} else if (target instanceof ParentNodeFolder) {
					if ((((ParentNodeFolder) target).getNode().getPath()
							.startsWith(SlcJcrResultUtils
									.getMyResultsBasePath(session))))
						targetParentNode = ((ParentNodeFolder) target)
								.getNode();
				} else if (target instanceof SingleResultNode) {
					Node currNode = ((SingleResultNode) target).getNode();
					if (currNode
							.getParent()
							.getPath()
							.startsWith(
									SlcJcrResultUtils
											.getMyResultsBasePath(session)))
						targetParentNode = currNode.getParent();
				}
				if (targetParentNode != null) {
					currParentNode = targetParentNode;
					validDrop = true;
					// FIXME
					lastSelectedTargetElement = (ResultParent) target;
					lastSelectedTargetElementParent = (ResultParent) ((ResultParent) target)
							.getParent();
				}
				// Check if it's a move or a copy
				if (validDrop) {
					String pPath = "";
					if (lastSelectedSourceElementParent instanceof ResultFolder)
						pPath = ((ResultFolder) lastSelectedSourceElementParent)
								.getNode().getPath();
					if ((pPath.startsWith(SlcJcrResultUtils
							.getMyResultsBasePath(session))))
						copyNode = false;
				}
			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while validating drop target", re);
			}
			return validDrop;
		}

		@Override
		public boolean performDrop(Object data) {

			try {
				Node source = session.getNodeByIdentifier((String) data);
				if (copyNode) {
					Node target = currParentNode.addNode(source.getName(),
							source.getPrimaryNodeType().getName());
					JcrUtils.copy(source, target);
					ResultParentUtils.updatePassedStatus(
							target,
							target.getNode(SlcNames.SLC_STATUS)
									.getProperty(SlcNames.SLC_SUCCESS)
									.getBoolean());
					target.getSession().save();
				} else // move only
				{
					String sourcePath = source.getPath();
					String destPath = currParentNode.getPath() + "/"
							+ source.getName();
					session.move(sourcePath, destPath);
					session.save();
					Node target = session.getNode(destPath);
					ResultParentUtils.updatePassedStatus(
							target,
							target.getNode(SlcNames.SLC_STATUS)
									.getProperty(SlcNames.SLC_SUCCESS)
									.getBoolean());
					session.save();
				}
			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while copying dropped node", re);
			}
			return true;
		}
	}

	class ResultObserver extends AsyncUiEventListener {

		public ResultObserver(Display display) {
			super(display);
		}

		@Override
		protected Boolean willProcessInUiThread(List<Event> events)
				throws RepositoryException {
			// unfiltered for the time being
			return true;
			// for (Event event : events) {
			// getLog().debug("Received event " + event);
			// int eventType = event.getType();
			// if (eventType == Event.NODE_REMOVED)
			// ;//return true;
			// String path = event.getPath();
			// int index = path.lastIndexOf('/');
			// String propertyName = path.substring(index + 1);
			// if (propertyName.equals(SlcNames.SLC_COMPLETED)
			// || propertyName.equals(SlcNames.SLC_UUID)) {
			// ;//return true;
			// }
			// }
			// return false;
		}

		protected void onEventInUiThread(List<Event> events)
				throws RepositoryException {

			for (Event event : events) {
				getLog().debug("Received event " + event);
				int eventType = event.getType();
				if (eventType == Event.NODE_REMOVED) {
					String path = event.getPath();
					int index = path.lastIndexOf('/');
					String parPath = path.substring(0, index + 1);
					if (session.nodeExists(parPath)) {
						Node currNode = session.getNode(parPath);
						if (currNode.isNodeType(NodeType.NT_UNSTRUCTURED)) {
							refresh(null);
							jcrRefresh(currNode);
							resultTreeViewer.refresh(true);
							resultTreeViewer.expandToLevel(
									lastSelectedTargetElementParent, 1);

						}
					}
				} else if (eventType == Event.NODE_ADDED) {
					String path = event.getPath();
					if (session.nodeExists(path)) {
						Node currNode = session.getNode(path);
						if (currNode.isNodeType(SlcTypes.SLC_DIFF_RESULT)
								|| currNode
										.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
							refresh(null);
							resultTreeViewer.expandToLevel(
									lastSelectedTargetElement, 1);
						}
					}
				}
			}
		}
	}

	class PropertiesContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			try {
				if (inputElement instanceof Node) {
					List<Property> props = new ArrayList<Property>();
					PropertyIterator pit = ((Node) inputElement)
							.getProperties();
					while (pit.hasNext())
						props.add(pit.nextProperty());
					return props.toArray();
				}
				return new Object[] {};
			} catch (RepositoryException e) {
				throw new ArgeoException("Cannot get element for "
						+ inputElement, e);
			}
		}
	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			processDoubleClick(evt);
		}

	}

	/* DEPENDENCY INJECTION */
	public void setSession(Session session) {
		this.session = session;
	}

}
