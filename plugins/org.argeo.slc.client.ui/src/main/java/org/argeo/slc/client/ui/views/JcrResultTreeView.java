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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcUiConstants;
import org.argeo.slc.client.ui.commands.AddResultFolder;
import org.argeo.slc.client.ui.commands.RenameResultFolder;
import org.argeo.slc.client.ui.commands.RenameResultNode;
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
import org.argeo.slc.client.ui.wizards.ConfirmOverwriteWizard;
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
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.wizard.WizardDialog;
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

	private final static Log log = LogFactory.getLog(JcrResultTreeView.class);

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer resultTreeViewer;
	private TableViewer propertiesViewer;

	private EventListener resultsObserver = null;

	private final static String[] observedNodeTypes = {
			SlcTypes.SLC_TEST_RESULT, SlcTypes.SLC_RESULT_FOLDER,
			NodeType.NT_UNSTRUCTURED };

	// FIXME cache to ease D&D
	private boolean isActionUnderMyResult = false;
	private ResultParent lastSelectedTargetElement;
	private ResultParent lastSelectedSourceElement;
	private ResultParent lastSelectedSourceElementParent;
	private boolean isResultFolder = false;

	// FIXME we cache the fact that we are moving a node to avoid exception
	// triggered by the "Add Node" event while moving
	// boolean isMoveInProgress = false;

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
		viewer.addSelectionChangedListener(new MySelectionChangedListener());
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
							Event.NODE_MOVED | Event.NODE_ADDED
									| Event.NODE_REMOVED, UserJcrUtils
									.getUserHome(session).getPath(), true,
							null, observedNodeTypes, false);
				} catch (RepositoryException e) {
					throw new SlcException("Cannot register listeners", e);
				}
			}

		} else {
			if (resultParent instanceof ParentNodeFolder) {
				ParentNodeFolder currFolder = (ParentNodeFolder) resultParent;
				jcrRefresh(currFolder.getNode());
				currFolder.forceFullRefresh();
			}
			// FIXME: specific refresh does not work
			// resultTreeViewer.refresh(currFolder, true);
			TreePath[] tps = resultTreeViewer.getExpandedTreePaths();
			resultTreeViewer.setInput(initializeResultTree());
			resultTreeViewer.setExpandedTreePaths(tps);
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
		boolean canAddSubfolder = false;
		boolean isSingleResultNode = false;
		if (selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				// if (obj instanceof ResultFolder
				// && (((ResultFolder) obj).getNode())
				// .isNodeType(SlcTypes.SLC_RESULT_FOLDER))
				if (isResultFolder)
					canAddSubfolder = true;
				else if (obj instanceof SingleResultNode)
					isSingleResultNode = true;
				else if (obj instanceof ParentNodeFolder
						&& (((ParentNodeFolder) obj).getNode().getPath()
								.startsWith(SlcJcrResultUtils
										.getMyResultsBasePath(session))))
					canAddSubfolder = true;

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
				canAddSubfolder);

		CommandUtils.refreshCommand(menuManager, window, RenameResultFolder.ID,
				RenameResultFolder.DEFAULT_LABEL,
				RenameResultFolder.DEFAULT_IMG_DESCRIPTOR, canAddSubfolder);

		CommandUtils.refreshCommand(menuManager, window, RenameResultNode.ID,
				RenameResultNode.DEFAULT_LABEL,
				RenameResultNode.DEFAULT_IMG_DESCRIPTOR, isSingleResultNode);
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
				try {
					Object obj = selection.getFirstElement();
					if (obj instanceof SingleResultNode) {
						Node tNode = ((SingleResultNode) obj).getNode();
						if (tNode.getPrimaryNodeType().isNodeType(
								SlcTypes.SLC_TEST_RESULT)) {
							doIt = true;
							isResultFolder = false;
						}
					} else if (obj instanceof ResultFolder) {
						Node tNode = ((ResultFolder) obj).getNode();
						if (tNode.getPrimaryNodeType().isNodeType(
								SlcTypes.SLC_RESULT_FOLDER)) {
							doIt = true;
							isResultFolder = true;
						}
					}
				} catch (RepositoryException re) {
					throw new SlcException(
							"unexpected error while validating drag source", re);
				}
			}
			event.doit = doIt;
		}

		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
					.getSelection();
			Object obj = selection.getFirstElement();
			try {
				Node first;
				if (obj instanceof SingleResultNode) {
					first = ((SingleResultNode) obj).getNode();
					event.data = first.getIdentifier();
				} else if (obj instanceof ResultFolder) {
					first = ((ResultFolder) obj).getNode();
					event.data = first.getIdentifier();
				}
			} catch (RepositoryException re) {
				throw new SlcException("unexpected error while setting data",
						re);
			}
		}

		public void dragFinished(DragSourceEvent event) {
			// refresh is done via observer
		}
	}

	// Implementation of the Drop Listener
	protected class ViewDropListener extends ViewerDropAdapter {
		private Node targetParentNode = null;

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {

			boolean validDrop = false;
			try {
				// We can only drop under myResults
				Node tpNode = null;
				if (target instanceof ResultFolder) {
					tpNode = ((ResultFolder) target).getNode();
				} else if (target instanceof ParentNodeFolder) {
					if ((((ParentNodeFolder) target).getNode().getPath()
							.startsWith(SlcJcrResultUtils
									.getMyResultsBasePath(session))))
						tpNode = ((ParentNodeFolder) target).getNode();
				} else if (target instanceof SingleResultNode) {
					Node currNode = ((SingleResultNode) target).getNode();
					if (currNode
							.getParent()
							.getPath()
							.startsWith(
									SlcJcrResultUtils
											.getMyResultsBasePath(session)))
						tpNode = currNode.getParent();
				}

				if (tpNode != null) {
					// Sanity check : we cannot move a folder to one of its sub
					// folder
					boolean doit = true;
					if (isResultFolder) {
						Node source = ((ParentNodeFolder) lastSelectedSourceElement)
								.getNode();
						String sourcePath = source.getPath();
						String targetPath = tpNode.getPath();
						if (targetPath.startsWith(sourcePath))
							doit = false;
					}
					if (doit) {
						targetParentNode = tpNode;
						validDrop = true;
						lastSelectedTargetElement = (ResultParent) target;
					}
				}

			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while validating drop target", re);
			}
			return validDrop;
		}

		@Override
		public boolean performDrop(Object data) {
			// clear selection to prevent unwanted scrolling of the UI
			resultTreeViewer.setSelection(null);
			try {
				Node source = session.getNodeByIdentifier((String) data);

				// Check is a node with same name already exists at destination
				String name;
				if (source.hasProperty(Property.JCR_TITLE))
					name = source.getProperty(Property.JCR_TITLE).getString();
				else if (source.hasProperty(SlcNames.SLC_TEST_CASE))
					name = source.getProperty(SlcNames.SLC_TEST_CASE)
							.getString();
				else
					name = source.getName();

				if (targetParentNode.hasNode(name)) {
					ConfirmOverwriteWizard wizard = new ConfirmOverwriteWizard(
							name, targetParentNode);
					WizardDialog dialog = new WizardDialog(Display.getDefault()
							.getActiveShell(), wizard);
					dialog.open();

					if (wizard.overwrite()) {
						targetParentNode.getNode(name).remove();
						// session.save();
					} else
						name = wizard.newName();
				}

				Node target;
				if (!isActionUnderMyResult) {// Copy
					target = targetParentNode.addNode(name, source
							.getPrimaryNodeType().getName());
					JcrUtils.copy(source, target);
				} else {// move
					String sourcePath = source.getPath();
					String destPath = targetParentNode.getPath() + "/" + name;
					session.move(sourcePath, destPath);
					// session.save();
					target = session.getNode(destPath);
				}
				if (!target.isNodeType(NodeType.MIX_TITLE))
					target.addMixin(NodeType.MIX_TITLE);
				target.setProperty(Property.JCR_TITLE, name);
				ResultParentUtils
						.updatePassedStatus(target,
								target.getNode(SlcNames.SLC_STATUS)
										.getProperty(SlcNames.SLC_SUCCESS)
										.getBoolean());
				session.save();
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
		}

		protected void onEventInUiThread(List<Event> events)
				throws RepositoryException {
			int i = 0;

			for (Event event : events) {
				i++;
				// log.debug("Received event " + event);
				int eventType = event.getType();
				if (eventType == Event.NODE_REMOVED) {
					String path = event.getPath();
					String parPath = JcrUtils.parentPath(path);
					if (session.nodeExists(parPath)) {
						Node currNode = session.getNode(parPath);
						if (currNode.isNodeType(NodeType.NT_UNSTRUCTURED)) {
							// jcrRefresh(currNode);
							refresh(lastSelectedSourceElementParent);
						}
					}
				} else if (eventType == Event.NODE_ADDED) {
					// refresh(lastSelectedTargetElement);
					String path = event.getPath();
					if (session.nodeExists(path)) {
						Node currNode = session.getNode(path);
						if (currNode.isNodeType(SlcTypes.SLC_TEST_RESULT)
								|| currNode
										.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
							refresh(lastSelectedTargetElement);
							// resultTreeViewer.expandToLevel(
							// lastSelectedTargetElement, 1);
						}
					}
				}
			}
			if (log.isDebugEnabled())
				log.debug("treated events: " + i);
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

	class MySelectionChangedListener implements ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty()) {
				IStructuredSelection sel = (IStructuredSelection) event
						.getSelection();
				ResultParent firstItem = (ResultParent) sel.getFirstElement();
				if (firstItem instanceof SingleResultNode)
					propertiesViewer.setInput(((SingleResultNode) firstItem)
							.getNode());
				else
					propertiesViewer.setInput(null);
				// update cache for Drag & drop
				lastSelectedTargetElement = firstItem;
				lastSelectedSourceElement = firstItem;
				lastSelectedSourceElementParent = (ResultParent) firstItem
						.getParent();
				String pPath = "";
				try {

					if (firstItem instanceof ParentNodeFolder)
						pPath = ((ParentNodeFolder) firstItem).getNode()
								.getPath();
					else if (firstItem instanceof SingleResultNode)
						pPath = ((SingleResultNode) firstItem).getNode()
								.getPath();
				} catch (RepositoryException e) {
					throw new SlcException(
							"Unexpected error while checking parent UI tree", e);
				}
				if ((pPath.startsWith(SlcJcrResultUtils
						.getMyResultsBasePath(session))))
					isActionUnderMyResult = true;
				else
					isActionUnderMyResult = false;
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
