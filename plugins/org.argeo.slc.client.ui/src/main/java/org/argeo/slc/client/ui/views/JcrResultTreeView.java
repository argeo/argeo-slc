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
package org.argeo.slc.client.ui.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
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
import org.argeo.slc.client.ui.model.ResultItemsComparator;
import org.argeo.slc.client.ui.model.ResultItemsComparer;
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
import org.eclipse.jface.dialogs.MessageDialog;
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

	private final static DateFormat dateFormat = new SimpleDateFormat(
			SlcUiConstants.DEFAULT_DISPLAY_DATE_TIME_FORMAT);

	// private final static Log log =
	// LogFactory.getLog(JcrResultTreeView.class);

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer resultTreeViewer;
	private TableViewer propertiesViewer;

	private EventListener myResultsObserver = null;
	private EventListener allResultsObserver = null;

	// under My Results
	private final static String[] observedNodeTypesUnderMyResult = {
			SlcTypes.SLC_TEST_RESULT, SlcTypes.SLC_RESULT_FOLDER,
			SlcTypes.SLC_MY_RESULT_ROOT_FOLDER };

	private final static String[] observedNodeTypesUnderAllResults = {
			SlcTypes.SLC_TEST_RESULT, NodeType.NT_UNSTRUCTURED };

	// FIXME cache to ease D&D
	// private boolean isActionUnderMyResult = false;
	// private ResultParent lastSelectedTargetElement;
	// private ResultParent lastSelectedSourceElement;
	// private ResultParent lastSelectedSourceElementParent;
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

		setOrderedInput(resultTreeViewer);

		// Initialize observer
		try {
			ObservationManager observationManager = session.getWorkspace()
					.getObservationManager();
			myResultsObserver = new MyResultsObserver(resultTreeViewer
					.getTree().getDisplay());
			allResultsObserver = new AllResultsObserver(resultTreeViewer
					.getTree().getDisplay());

			// observe tree changes under MyResults
			observationManager.addEventListener(myResultsObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED,
					SlcJcrResultUtils.getMyResultsBasePath(session), true,
					null, observedNodeTypesUnderMyResult, false);
			// observe tree changes under All results
			observationManager.addEventListener(allResultsObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED,
					SlcJcrResultUtils.getSlcResultsBasePath(session), true,
					null, observedNodeTypesUnderAllResults, false);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listeners", e);
		}
	}

	/**
	 * Override default behaviour so that default defined order remains
	 * unchanged on first level of the tree
	 */
	private void setOrderedInput(TreeViewer viewer) {
		// Add specific ordering
		resultTreeViewer.setInput(null);
		viewer.setComparator(null);
		resultTreeViewer.setInput(initializeResultTree());
		viewer.setComparator(new ResultItemsComparator());
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

		// Override default behaviour to insure that 2 distincts results that
		// have the same name will be correctly and distincly returned by
		// corresponding
		// TreeViewer.getSelection() method.
		viewer.setComparer(new ResultItemsComparer());

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
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				try {
					String name = ((Property) element).getName();
					String value = null;
					if (SlcNames.SLC_TEST_CASE.equals(name))
						value = "Test case";
					else if (SlcNames.SLC_COMPLETED.equals(name))
						value = "Completed on";
					else if (SlcNames.SLC_SUCCESS.equals(name))
						value = "Status";
					else if (SlcNames.SLC_MESSAGE.equals(name))
						value = "Message";
					else if (SlcNames.SLC_ERROR_MESSAGE.equals(name))
						value = "Error";
					return value;
				} catch (RepositoryException e) {
					throw new ArgeoException(
							"Unexpected exception in label provider", e);
				}
			}
		});
		col = new TableViewerColumn(propertiesViewer, SWT.NONE);
		col.getColumn().setText("Value");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				try {
					Property property = (Property) element;
					String name = property.getName();
					String value = null;

					if (SlcNames.SLC_TEST_CASE.equals(name)
							|| SlcNames.SLC_ERROR_MESSAGE.equals(name)
							|| SlcNames.SLC_MESSAGE.equals(name))
						value = property.getValue().getString();
					else if (SlcNames.SLC_COMPLETED.equals(name)) {
						Calendar date = property.getValue().getDate();
						value = dateFormat.format(date.getTime());
					} else if (SlcNames.SLC_SUCCESS.equals(name)) {
						if (property.getValue().getBoolean())
							value = "SUCCESS";
						else {
							if (property.getParent().hasNode(
									SlcNames.SLC_ERROR_MESSAGE))
								value = "ERROR";
							else
								value = "FAILED";
						}
					}
					return value;
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
	 * refreshes the passed resultParent and its corresponding subtree. It
	 * refreshes the whole viewer if null is passed.
	 * 
	 * @param ResultParent
	 * 
	 */
	public void refresh(ResultParent resultParent) {
		if (resultParent == null) {
			if (!resultTreeViewer.getTree().isDisposed()) {
				TreePath[] tps = resultTreeViewer.getExpandedTreePaths();
				setOrderedInput(resultTreeViewer);
				resultTreeViewer.setExpandedTreePaths(tps);
			} else
				setOrderedInput(resultTreeViewer);
		} else {
			if (resultParent instanceof ParentNodeFolder) {
				ParentNodeFolder currFolder = (ParentNodeFolder) resultParent;
				jcrRefresh(currFolder.getNode());
				currFolder.forceFullRefresh();
			}
			// FIXME: specific refresh does not work
			// resultTreeViewer.refresh(resultParent, true);
			refresh(null);
		}
	}

	/**
	 * refreshes the passed node and its corresponding subtree.
	 * 
	 * @param node
	 *            cannot be null
	 * 
	 */
	public boolean jcrRefresh(Node node) {
		// if (log.isDebugEnabled())
		// log.debug(" JCR refreshing " + node + "...");
		// Thread.dumpStack();
		boolean isPassed = true;
		try {
			if (node.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
				isPassed = node.getNode(SlcNames.SLC_AGGREGATED_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean();
			} else if (node.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
				NodeIterator ni = node.getNodes();
				while (ni.hasNext()) {
					Node currChild = ni.nextNode();
					isPassed = isPassed & jcrRefresh(currChild);
				}
				if (isPassed != node.getNode(SlcNames.SLC_AGGREGATED_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean()) {
					node.getNode(SlcNames.SLC_AGGREGATED_STATUS).setProperty(
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

	private ResultParent[] initializeResultTree() {
		try {
			// Force initialization of the tree structure if needed
			SlcJcrResultUtils.getSlcResultsParentNode(session);
			SlcJcrResultUtils.getMyResultParentNode(session);
			// Remove yesterday and last 7 days virtual folders
			// ResultParent[] roots = new ResultParent[5];
			ResultParent[] roots = new ResultParent[3];

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
					ResultParentUtils.getResultsForDates(session, datePathes),
					"Today");

			// // Yesterday
			// cal = Calendar.getInstance();
			// cal.add(Calendar.DAY_OF_YEAR, -1);
			// relPath = JcrUtils.dateAsPath(cal);
			// datePathes = new ArrayList<String>();
			// datePathes.add(relPath);
			// roots[2] = new VirtualFolder(null,
			// ResultParentUtils.getResultsForDates(session, datePathes),
			// "Yesterday");
			// // Last 7 days
			//
			// cal = Calendar.getInstance();
			// datePathes = new ArrayList<String>();
			//
			// for (int i = 0; i < 7; i++) {
			// cal.add(Calendar.DAY_OF_YEAR, -i);
			// relPath = JcrUtils.dateAsPath(cal);
			// datePathes.add(relPath);
			// }
			// roots[3] = new VirtualFolder(null,
			// ResultParentUtils.getResultsForDates(session, datePathes),
			// "Last 7 days");

			// All results
			Node otherResultsPar = session.getNode(SlcJcrResultUtils
					.getSlcResultsBasePath(session));
			// roots[4] = new ParentNodeFolder(null, otherResultsPar,
			// "All results");
			roots[2] = new ParentNodeFolder(null, otherResultsPar,
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

		IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
				.getSelection();
		boolean canAddSubfolder = false;
		boolean canRenamefolder = false;
		boolean isSingleResultNode = false;
		// Building conditions
		if (selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				if (obj instanceof SingleResultNode)
					isSingleResultNode = true;
				else if (obj instanceof ParentNodeFolder) {
					Node cNode = ((ParentNodeFolder) obj).getNode();
					if (cNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
						canAddSubfolder = true;
						canRenamefolder = true;
					} else if (cNode
							.isNodeType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER)) {
						canAddSubfolder = true;
					}
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
				canAddSubfolder);

		CommandUtils.refreshCommand(menuManager, window, RenameResultFolder.ID,
				RenameResultFolder.DEFAULT_LABEL,
				RenameResultFolder.DEFAULT_IMG_DESCRIPTOR, canRenamefolder);

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

			// FIXME clean this code.
			try {
				if (selection.size() == 1) {
					Object obj = selection.getFirstElement();
					if (obj instanceof ResultFolder) {
						Node tNode = ((ResultFolder) obj).getNode();
						if (tNode.getPrimaryNodeType().isNodeType(
								SlcTypes.SLC_RESULT_FOLDER)) {
							doIt = true;
							isResultFolder = true;
						}
					} else
						isResultFolder = false;
				} else
					isResultFolder = false;

				if (!isResultFolder) {
					@SuppressWarnings("rawtypes")
					Iterator it = selection.iterator();
					while (it.hasNext()) {
						Object obj = it.next();
						if (obj instanceof SingleResultNode) {
							Node tNode = ((SingleResultNode) obj).getNode();
							if (tNode.getPrimaryNodeType().isNodeType(
									SlcTypes.SLC_TEST_RESULT)) {
								doIt = true;
							}
						}
					}
				}

			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while validating drag source", re);
			}
			event.doit = doIt;
		}

		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) resultTreeViewer
					.getSelection();

			try {
				// specific case of a result folder
				if (isResultFolder) {
					Object obj = selection.getFirstElement();
					event.data = ((ResultFolder) obj).getNode().getIdentifier();
				} else {
					@SuppressWarnings("rawtypes")
					Iterator it = selection.iterator();
					StringBuffer nodes = new StringBuffer();
					while (it.hasNext()) {
						Object obj = it.next();
						if (obj instanceof SingleResultNode) {
							Node tNode = ((SingleResultNode) obj).getNode();
							if (tNode.getPrimaryNodeType().isNodeType(
									SlcTypes.SLC_TEST_RESULT)) {
								nodes.append(tNode.getIdentifier() + ";");
							}
						}
					}
					event.data = nodes.toString();
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
				if (target instanceof SingleResultNode) {
					Node currNode = ((SingleResultNode) target).getNode();
					String pPath = currNode.getParent().getPath();
					if (pPath.startsWith(SlcJcrResultUtils
							.getMyResultsBasePath(session)))
						tpNode = currNode.getParent();
				} else if (target instanceof ResultFolder) {
					tpNode = ((ResultFolder) target).getNode();
				} else if (target instanceof ParentNodeFolder) {
					Node node = ((ParentNodeFolder) target).getNode();
					if (node.isNodeType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER))
						tpNode = ((ParentNodeFolder) target).getNode();
				}

				if (tpNode != null) {
					targetParentNode = tpNode;
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
			// clear selection to prevent unwanted scrolling of the UI
			resultTreeViewer.setSelection(null);
			try {
				if (isResultFolder) {
					// Sanity check : we cannot move a folder to one of its sub
					// folder or neither move an object in the same parent
					// folder
					Node source = session.getNodeByIdentifier((String) data);
					if (targetParentNode.getPath().startsWith(source.getPath())
							|| source.getParent().getPath()
									.equals(targetParentNode.getPath()))
						return false;

					// Move
					String sourcePath = source.getPath();
					String destPath = targetParentNode.getPath() + "/"
							+ source.getName();
					session.move(sourcePath, destPath);
					// Update passed status of the parent source Node
					ResultParentUtils.updatePassedStatus(
							session.getNode(JcrUtils.parentPath(sourcePath)),
							true);
					// Node target = session.getNode(destPath);
					session.save();
					return true;
				}

				String[] datas = ((String) data).split(";");
				nodesToCopy: for (String id : datas) {

					Node source = session.getNodeByIdentifier(id);
					String name;
					if (source.hasProperty(Property.JCR_TITLE))
						name = source.getProperty(Property.JCR_TITLE)
								.getString();
					else if (source.hasProperty(SlcNames.SLC_TEST_CASE))
						name = source.getProperty(SlcNames.SLC_TEST_CASE)
								.getString();
					else
						name = source.getName();

					// Check if another copy of the same test instance already
					// exists at target
					NodeIterator ni = targetParentNode.getNodes();
					String slcUid = source.getProperty(SlcNames.SLC_UUID)
							.getString();
					while (ni.hasNext()) {
						Node curr = ni.nextNode();
						if (curr.hasProperty(SlcNames.SLC_UUID)
								&& slcUid.equals(curr.getProperty(
										SlcNames.SLC_UUID).getString())) {
							MessageDialog
									.openWarning(
											PlatformUI.getWorkbench()
													.getDisplay()
													.getActiveShell(),
											"Duplicated instance.",
											"An instance of the same test case ("
													+ name
													+ ") exists at destination.\n "
													+ "This item will not be neither copied nor moved.");
							continue nodesToCopy;

						}
					}

					Node target;
					boolean passedStatus = source
							.getNode(SlcNames.SLC_AGGREGATED_STATUS)
							.getProperty(SlcNames.SLC_SUCCESS).getBoolean();

					boolean isActionUnderMyResult = source.getPath()
							.startsWith(
									SlcJcrResultUtils
											.getMyResultsBasePath(session));

					if (!isActionUnderMyResult) {// Copy
						target = targetParentNode.addNode(source.getName(),
								source.getPrimaryNodeType().getName());
						JcrUtils.copy(source, target);
					} else {// move
						String sourcePath = source.getPath();
						String destPath = targetParentNode.getPath() + "/"
								+ name;
						session.move(sourcePath, destPath);
						// session.save();
						// Update passed status of the parent source Node
						ResultParentUtils
								.updatePassedStatus(session.getNode(JcrUtils
										.parentPath(sourcePath)), true);
						target = session.getNode(destPath);

					}
					if (!target.isNodeType(NodeType.MIX_TITLE))
						target.addMixin(NodeType.MIX_TITLE);
					target.setProperty(Property.JCR_TITLE, name);
					ResultParentUtils.updatePassedStatus(target.getParent(),
							passedStatus);
					session.save();

					// try {
					// Node source = session.getNodeByIdentifier(id);
					// String name;
					// if (source.hasProperty(Property.JCR_TITLE))
					// name = source.getProperty(Property.JCR_TITLE)
					// .getString();
					// else if (source.hasProperty(SlcNames.SLC_TEST_CASE))
					// name = source.getProperty(SlcNames.SLC_TEST_CASE)
					// .getString();
					// else
					// name = source.getName();
					//
					// // Check if a user defined folder result with same name
					// // exists
					// // at target
					// if (targetParentNode.hasNode(name)
					// && targetParentNode.getNode(name).isNodeType(
					// SlcTypes.SLC_RESULT_FOLDER)) {
					// ConfirmOverwriteWizard wizard = new
					// ConfirmOverwriteWizard(
					// name, targetParentNode);
					// WizardDialog dialog = new WizardDialog(Display
					// .getDefault().getActiveShell(), wizard);
					//
					// if (dialog.open() == WizardDialog.CANCEL)
					// return true;
					//
					// if (wizard.overwrite()) {
					// targetParentNode.getNode(name).remove();
					// // session.save();
					// } else
					// name = wizard.newName();
					// }
					//
					// Node target;
					// boolean passedStatus = source
					// .getNode(SlcNames.SLC_AGGREGATED_STATUS)
					// .getProperty(SlcNames.SLC_SUCCESS).getBoolean();
					//
					// if (!isActionUnderMyResult) {// Copy
					// target = targetParentNode.addNode(source.getName(),
					// source.getPrimaryNodeType().getName());
					// JcrUtils.copy(source, target);
					// } else {// move
					// String sourcePath = source.getPath();
					// String destPath = targetParentNode.getPath() + "/"
					// + name;
					// session.move(sourcePath, destPath);
					// // session.save();
					// // Update passed status of the parent source Node
					// ResultParentUtils
					// .updatePassedStatus(session.getNode(JcrUtils
					// .parentPath(sourcePath)), true);
					// target = session.getNode(destPath);
					//
					// }
					// if (!target.isNodeType(NodeType.MIX_TITLE))
					// target.addMixin(NodeType.MIX_TITLE);
					// target.setProperty(Property.JCR_TITLE, name);
					// ResultParentUtils.updatePassedStatus(target.getParent(),
					// passedStatus);
					// session.save();

				}
			} catch (RepositoryException re) {
				throw new SlcException(
						"unexpected error while copying dropped node", re);

			}
			return true;
		}
	}

	class MyResultsObserver extends AsyncUiEventListener {

		public MyResultsObserver(Display display) {
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
			List<Node> nodesToRefresh = new ArrayList<Node>();

			for (Event event : events) {
				String parPath = JcrUtils.parentPath(event.getPath());
				if (session.nodeExists(parPath)) {
					Node node = session.getNode(parPath);
					if (!nodesToRefresh.contains(node)) {
						nodesToRefresh.add(node);
					}
				}
			}

			// Update check nodes
			for (Node node : nodesToRefresh)
				jcrRefresh(node);
			refresh(null);
		}
	}

	class AllResultsObserver extends AsyncUiEventListener {

		public AllResultsObserver(Display display) {
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
			refresh(null);
			// if (lastSelectedSourceElementParent != null)
			// refresh(lastSelectedSourceElementParent);
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
					Node node = (Node) inputElement;
					if (node.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
						List<Property> props = new ArrayList<Property>();
						if (node.hasProperty(SlcNames.SLC_TEST_CASE))
							props.add(node.getProperty(SlcNames.SLC_TEST_CASE));
						if (node.hasProperty(SlcNames.SLC_COMPLETED))
							props.add(node.getProperty(SlcNames.SLC_COMPLETED));
						if (node.hasNode(SlcNames.SLC_AGGREGATED_STATUS)) {
							Node status = node
									.getNode(SlcNames.SLC_AGGREGATED_STATUS);
							props.add(status.getProperty(SlcNames.SLC_SUCCESS));
							if (status.hasProperty(SlcNames.SLC_MESSAGE))
								props.add(status
										.getProperty(SlcNames.SLC_MESSAGE));
							if (status.hasProperty(SlcNames.SLC_ERROR_MESSAGE))
								props.add(status
										.getProperty(SlcNames.SLC_ERROR_MESSAGE));
						}
						return props.toArray();
					}
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
				// lastSelectedTargetElement = firstItem;
				// lastSelectedSourceElement = firstItem;
				// lastSelectedSourceElementParent = (ResultParent) firstItem
				// .getParent();
				// String pPath = "";
				// try {
				//
				// if (firstItem instanceof ParentNodeFolder)
				// pPath = ((ParentNodeFolder) firstItem).getNode()
				// .getPath();
				// else if (firstItem instanceof SingleResultNode)
				// pPath = ((SingleResultNode) firstItem).getNode()
				// .getPath();
				// } catch (RepositoryException e) {
				// throw new SlcException(
				// "Unexpected error while checking parent UI tree", e);
				// }
				// if ((pPath.startsWith(SlcJcrResultUtils
				// .getMyResultsBasePath(session))))
				// isActionUnderMyResult = true;
				// else
				// isActionUnderMyResult = false;
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