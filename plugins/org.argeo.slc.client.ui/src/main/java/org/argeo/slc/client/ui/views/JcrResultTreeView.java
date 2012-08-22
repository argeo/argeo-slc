package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.commands.AddResultFolder;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.SimpleNodeFolder;
import org.argeo.slc.client.ui.model.SingleResultNode;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

/** SLC generic JCR Result tree view. */
public class JcrResultTreeView extends ViewPart {
	public final static String ID = ClientUiPlugin.ID + ".jcrResultTreeView";

	private final static Log log = LogFactory.getLog(JcrResultTreeView.class);

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer resultTreeViewer;
	private TableViewer propertiesViewer;

	private EventListener resultsObserver = null;

	private final static String[] observedNodeTypes = { SlcTypes.SLC_TEST_RESULT };

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

		try {
			ObservationManager observationManager = session.getWorkspace()
					.getObservationManager();
			// FIXME Will not be notified if empty result is deleted
			if (ArgeoJcrUtils.getUserHome(session) != null) {
				resultsObserver = new ResultObserver(resultTreeViewer.getTree()
						.getDisplay());
				observationManager.addEventListener(resultsObserver,
						Event.PROPERTY_ADDED | Event.NODE_REMOVED, ArgeoJcrUtils
								.getUserHome(session).getPath(), true, null,
						observedNodeTypes, false);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listeners", e);
		}

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
				// force initialization of the resultsObserver, only useful
				// if the current view has been displayed before a single
				// test has been run
				try {
					ObservationManager observationManager = session
							.getWorkspace().getObservationManager();
					resultsObserver = new ResultObserver(resultTreeViewer
							.getTree().getDisplay());
					observationManager.addEventListener(resultsObserver,
							Event.PROPERTY_ADDED | Event.NODE_REMOVED, ArgeoJcrUtils
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
			}
		}
	}

	private ResultParent[] initializeResultTree() {
		ResultParent[] roots = new ResultParent[2];
		try {
			roots[0] = new ResultFolder(null,
					SlcJcrResultUtils.getMyResultParentNode(session),
					"My results");
			Node otherResultsPar = session.getNode(SlcJcrResultUtils
					.getSlcResultsBasePath(session));
			roots[1] = new SimpleNodeFolder(null, otherResultsPar,
					"All results");
			return roots;
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
				Node targetParentNode = null;
				if (obj instanceof ResultFolder) {
					targetParentNode = ((ResultFolder) obj).getNode();

					if (targetParentNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
						isMyResultFolder = true;
				}
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
						if (tNode.getPrimaryNodeType().isNodeType(
								SlcTypes.SLC_TEST_RESULT)
								&& (tNode.getPath()
										.startsWith(SlcJcrResultUtils
												.getSlcResultsBasePath(session))))
							doIt = true;
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
					Node currNode = ((ResultFolder) target).getNode();

					if (currNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
						targetParentNode = currNode;
					}
				} else if (target instanceof SingleResultNode) {
					Node currNode = ((SingleResultNode) target).getNode();
					if (currNode.getParent().isNodeType(
							SlcTypes.SLC_RESULT_FOLDER))
						targetParentNode = currNode.getParent();
				}
				if (targetParentNode != null) {
					currParentNode = targetParentNode;
					validDrop = true;
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
				Node target = currParentNode.addNode(source.getName(), source
						.getPrimaryNodeType().getName());
				JcrUtils.copy(source, target);
				updatePassedStatus(target, target.getNode(SlcNames.SLC_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean());
				target.getSession().save();
			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while copying dropped node", re);
			}
			return true;
		}

		/**
		 * recursively update passed status of the parent ResultFolder and its
		 * parent if needed
		 * 
		 * @param node
		 *            cannot be null
		 * 
		 */
		private void updatePassedStatus(Node node, boolean passed) {
			try {
				Node pNode = node.getParent();
				boolean pStatus = pNode.getNode(SlcNames.SLC_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean();
				if (pStatus == passed)
					// nothing to update
					return;
				else if (!passed) {
					// error we only update status of the result folder and its
					// parent if needed
					pNode.getNode(SlcNames.SLC_STATUS).setProperty(
							SlcNames.SLC_SUCCESS, passed);
					updatePassedStatus(pNode, passed);
				} else {
					// success we must first check if all siblings have also
					// successfully completed
					boolean success = true;
					NodeIterator ni = pNode.getNodes();
					children: while (ni.hasNext()) {
						Node currNode = ni.nextNode();
						if ((currNode.isNodeType(SlcTypes.SLC_DIFF_RESULT) || currNode
								.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
								&& !currNode.getNode(SlcNames.SLC_STATUS)
										.getProperty(SlcNames.SLC_SUCCESS)
										.getBoolean()) {
							success = false;
							break children;
						}
					}
					if (success) {
						pNode.getNode(SlcNames.SLC_STATUS).setProperty(
								SlcNames.SLC_SUCCESS, passed);
						updatePassedStatus(pNode, passed);
					} else
						// one of the siblings had also the failed status so
						// above tree remains unchanged.
						return;
				}

			} catch (RepositoryException e) {
				throw new SlcException("Cannot register listeners", e);
			}
		}
	}

	class ResultObserver extends AsyncUiEventListener {

		public ResultObserver(Display display) {
			super(display);
		}

		@Override
		protected Boolean willProcessInUiThread(List<Event> events)
				throws RepositoryException {
			for (Event event : events) {
				// getLog().debug("Received event " + event);
				int eventType = event.getType();
				if (eventType == Event.NODE_REMOVED)
					return true;
				String path = event.getPath();
				int index = path.lastIndexOf('/');
				String propertyName = path.substring(index + 1);
				if (propertyName.equals(SlcNames.SLC_COMPLETED)
						|| propertyName.equals(SlcNames.SLC_UUID)) {
					return true;
				}
			}
			return false;
		}

		protected void onEventInUiThread(List<Event> events)
				throws RepositoryException {
			// FIXME implement correct behaviour. treeViewer selection is
			// disposed by the drag & drop.
			// resultTreeViewer.refresh();
			// refresh(null);
			// log.warn("Implement refresh.");
		}

	}

	class PropertiesContentProvider implements IStructuredContentProvider {
		// private JcrItemsComparator itemComparator = new JcrItemsComparator();

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

	/* DEPENDENCY INJECTION */
	public void setSession(Session session) {
		this.session = session;
	}

}