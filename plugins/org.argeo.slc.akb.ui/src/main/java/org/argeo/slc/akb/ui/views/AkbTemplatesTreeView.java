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
package org.argeo.slc.akb.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.providers.AkbTreeLabelProvider;
import org.argeo.slc.akb.ui.providers.TemplatesTreeContentProvider;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

/** SLC generic JCR Result tree view. */
public class AkbTemplatesTreeView extends ViewPart {
	// private final static Log log =
	// LogFactory.getLog(JcrResultTreeView.class);

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".akbTemplatesTreeView";

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer resultTreeViewer;

	// Usefull business objects
	private Node templatesParentNode;

	// private EventListener myResultsObserver = null;
	// private EventListener allResultsObserver = null;
	//
	// // under My Results
	// private final static String[] observedNodeTypesUnderMyResult = {
	// SlcTypes.SLC_TEST_RESULT, SlcTypes.SLC_RESULT_FOLDER,
	// SlcTypes.SLC_MY_RESULT_ROOT_FOLDER };
	//
	// private final static String[] observedNodeTypesUnderAllResults = {
	// SlcTypes.SLC_TEST_RESULT, NodeType.NT_UNSTRUCTURED };
	//
	// private boolean isResultFolder = false;

	// /**
	// * To be overridden to adapt size of form and result frames.
	// */
	// protected int[] getWeights() {
	// return new int[] { 70, 30 };
	// }

	private void initialize() {
		try {
			templatesParentNode = session
					.getNode(AkbNames.AKB_TEMPLATES_BASE_PATH);
		} catch (RepositoryException e) {
			throw new AkbException("unable to initialize AKB Browser view", e);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		initialize();

		createResultsTreeViewer(parent);

		// parent.setLayout(new FillLayout());
		// // Main layout
		// SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		// sashForm.setSashWidth(4);
		// sashForm.setLayout(new FillLayout());

		// Create the tree on top of the view
		// Composite top = new Composite(sashForm, SWT.NONE);
		// GridLayout gl = new GridLayout(1, false);
		// top.setLayout(gl);
		// resultTreeViewer = createResultsTreeViewer(top);

		// // Create the property viewer on the bottom
		// Composite bottom = new Composite(sashForm, SWT.NONE);
		// bottom.setLayout(new GridLayout(1, false));
		// propertiesViewer = createPropertiesViewer(bottom);
		//
		// sashForm.setWeights(getWeights());

		// setOrderedInput(resultTreeViewer);

		// Initialize observer
		// try {
		// ObservationManager observationManager = session.getWorkspace()
		// .getObservationManager();
		// myResultsObserver = new MyResultsObserver(resultTreeViewer
		// .getTree().getDisplay());
		// allResultsObserver = new AllResultsObserver(resultTreeViewer
		// .getTree().getDisplay());
		//
		// // observe tree changes under MyResults
		// observationManager.addEventListener(myResultsObserver,
		// Event.NODE_ADDED | Event.NODE_REMOVED,
		// SlcJcrResultUtils.getMyResultsBasePath(session), true,
		// null, observedNodeTypesUnderMyResult, false);
		// // observe tree changes under All results
		// observationManager.addEventListener(allResultsObserver,
		// Event.NODE_ADDED | Event.NODE_REMOVED,
		// SlcJcrResultUtils.getSlcResultsBasePath(session), true,
		// null, observedNodeTypesUnderAllResults, false);
		// } catch (RepositoryException e) {
		// throw new SlcException("Cannot register listeners", e);
		// }
	}

	/**
	 * Override default behaviour so that default defined order remains
	 * unchanged on first level of the tree
	 */
	// private void setOrderedInput(TreeViewer viewer) {
	// // Add specific ordering
	// viewer.setInput(null);
	// viewer.setComparator(null);
	// viewer.setInput(initializeResultTree());
	// viewer.setComparator(new ResultItemsComparator());
	// }

	// The main tree viewer
	protected TreeViewer createResultsTreeViewer(Composite parent) {
		int style = SWT.BORDER | SWT.MULTI;

		TreeViewer viewer = new TreeViewer(parent, style);
		viewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new TemplatesTreeContentProvider());
		viewer.setLabelProvider(new AkbTreeLabelProvider());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

		// Add label provider with label decorator
		// ResultTreeLabelProvider rtLblProvider = new
		// ResultTreeLabelProvider();
		// ILabelDecorator decorator = AkbUiPlugin.getDefault().getWorkbench()
		// .getDecoratorManager().getLabelDecorator();
		// viewer.setLabelProvider(new DecoratingLabelProvider(rtLblProvider,
		// decorator));
		// viewer.addDoubleClickListener(new ViewDoubleClickListener());

		// Override default behaviour to insure that 2 distincts results that
		// have the same name will be correctly and distincly returned by
		// corresponding TreeViewer.getSelection() method.
		// viewer.setComparer(new ResultItemsComparer());

		getSite().setSelectionProvider(viewer);

		// // add drag & drop support
		// int operations = DND.DROP_COPY | DND.DROP_MOVE;
		// Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		// viewer.addDragSupport(operations, tt, new ViewDragListener());
		// viewer.addDropSupport(operations, tt, new ViewDropListener(viewer));

		// add context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTree().setMenu(menu);
		menuManager.setRemoveAllWhenShown(true);

		getSite().registerContextMenu(menuManager, viewer);

		// add change listener to display TestResult information in the property
		// viewer
		// viewer.addSelectionChangedListener(new MySelectionChangedListener());
		return viewer;
	}

	// Detailed property viewer
	// protected TableViewer createPropertiesViewer(Composite parent) {
	// }

	@Override
	public void setFocus() {
	}

	// private ResultParent[] initializeResultTree() {
	// try {
	// // Force initialization of the tree structure if needed
	// SlcJcrResultUtils.getSlcResultsParentNode(session);
	// SlcJcrResultUtils.getMyResultParentNode(session);
	// // Remove yesterday and last 7 days virtual folders
	// // ResultParent[] roots = new ResultParent[5];
	// ResultParent[] roots = new ResultParent[3];
	//
	// // My results
	// roots[0] = new ParentNodeFolder(null,
	// SlcJcrResultUtils.getMyResultParentNode(session),
	// SlcUiConstants.DEFAULT_MY_RESULTS_FOLDER_LABEL);
	//
	// // today
	// Calendar cal = Calendar.getInstance();
	// String relPath = JcrUtils.dateAsPath(cal);
	// List<String> datePathes = new ArrayList<String>();
	// datePathes.add(relPath);
	// roots[1] = new VirtualFolder(null,
	// ResultParentUtils.getResultsForDates(session, datePathes),
	// "Today");
	//
	// // // Yesterday
	// // cal = Calendar.getInstance();
	// // cal.add(Calendar.DAY_OF_YEAR, -1);
	// // relPath = JcrUtils.dateAsPath(cal);
	// // datePathes = new ArrayList<String>();
	// // datePathes.add(relPath);
	// // roots[2] = new VirtualFolder(null,
	// // ResultParentUtils.getResultsForDates(session, datePathes),
	// // "Yesterday");
	// // // Last 7 days
	// //
	// // cal = Calendar.getInstance();
	// // datePathes = new ArrayList<String>();
	// //
	// // for (int i = 0; i < 7; i++) {
	// // cal.add(Calendar.DAY_OF_YEAR, -i);
	// // relPath = JcrUtils.dateAsPath(cal);
	// // datePathes.add(relPath);
	// // }
	// // roots[3] = new VirtualFolder(null,
	// // ResultParentUtils.getResultsForDates(session, datePathes),
	// // "Last 7 days");
	//
	// // All results
	// Node otherResultsPar = session.getNode(SlcJcrResultUtils
	// .getSlcResultsBasePath(session));
	// // roots[4] = new ParentNodeFolder(null, otherResultsPar,
	// // "All results");
	// roots[2] = new ParentNodeFolder(null, otherResultsPar,
	// "All results");
	// return roots;
	// } catch (RepositoryException re) {
	// throw new ArgeoException(
	// "Unexpected error while initializing ResultTree.", re);
	// }
	// }

	// Manage context menu
	/**
	 * Defines the commands that will pop up in the context menu.
	 **/
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = AkbUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		try {
			// IStructuredSelection selection = (IStructuredSelection)
			// resultTreeViewer
			// .getSelection();
			// boolean canAddSubfolder = false;
			// boolean canRenamefolder = false;
			// boolean isSingleResultNode = false;
			// boolean isUnderMyResult = false;
			// boolean validMultipleDelete = false;
			//
			// // Building conditions
			// if (selection.size() == 1) {
			// Object obj = selection.getFirstElement();
			// if (obj instanceof SingleResultNode)
			// isSingleResultNode = true;
			// else if (obj instanceof ParentNodeFolder) {
			// Node cNode = ((ParentNodeFolder) obj).getNode();
			// if (cNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
			// canAddSubfolder = true;
			// canRenamefolder = true;
			// isUnderMyResult = true;
			// } else if (cNode
			// .isNodeType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER)) {
			// canAddSubfolder = true;
			// }
			// }
			// } else {
			// @SuppressWarnings("rawtypes")
			// Iterator it = selection.iterator();
			// multicheck: while (it.hasNext()) {
			// validMultipleDelete = true;
			// Object obj = it.next();
			// if (obj instanceof SingleResultNode)
			// continue multicheck;
			// else if (obj instanceof ParentNodeFolder) {
			// Node cNode = ((ParentNodeFolder) obj).getNode();
			// if (cNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
			// continue multicheck;
			// else {
			// validMultipleDelete = false;
			// break multicheck;
			// }
			// } else {
			// validMultipleDelete = false;
			// break multicheck;
			// }
			// }
			// }

			Map<String, String> params = new HashMap<String, String>();
			params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
					templatesParentNode.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_ENV_TEMPLATE);

			// Effective Refresh
			CommandUtils.refreshParametrizedCommand(menuManager, window,
					OpenAkbNodeEditor.ID, "Create new template...", null, true,
					params);

			//
			// CommandUtils.refreshCommand(menuManager, window, DeleteItems.ID,
			// DeleteItems.DEFAULT_LABEL, DeleteItems.DEFAULT_IMG_DESCRIPTOR,
			// isUnderMyResult || isSingleResultNode || validMultipleDelete);
			//
			// CommandUtils.refreshCommand(menuManager, window,
			// AddResultFolder.ID,
			// AddResultFolder.DEFAULT_LABEL,
			// ClientUiPlugin.getDefault().getWorkbench().getSharedImages()
			// .getImageDescriptor(ISharedImages.IMG_OBJ_ADD),
			// canAddSubfolder);
			//
			// CommandUtils.refreshCommand(menuManager, window,
			// RenameResultFolder.ID,
			// RenameResultFolder.DEFAULT_LABEL,
			// RenameResultFolder.DEFAULT_IMG_DESCRIPTOR, canRenamefolder);
			//
			// // Command removed for the time being.
			// CommandUtils.refreshCommand(menuManager, window,
			// RenameResultNode.ID,
			// RenameResultNode.DEFAULT_LABEL,
			// RenameResultNode.DEFAULT_IMG_DESCRIPTOR, false);
			//
			// // Test to be removed
			// // If you use this pattern, do not forget to call
			// // menuManager.setRemoveAllWhenShown(true);
			// // when creating the menuManager
			//
			// // menuManager.add(new Action("Test") {
			// // public void run() {
			// // log.debug("do something");
			// // }
			// // });
		} catch (RepositoryException re) {
			throw new AkbException("Error while refreshing context menu", re);
		}
	}

	/* INNER CLASSES */
	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					CommandUtils.callCommand(OpenAkbNodeEditor.ID,
							OpenAkbNodeEditor.PARAM_NODE_JCR_ID,
							node.getIdentifier());
				}
			} catch (RepositoryException e) {
				throw new AkbException("Cannot open " + obj, e);
			}
		}
	}

	// class MyResultsObserver extends AsyncUiEventListener {
	//
	// public MyResultsObserver(Display display) {
	// super(display);
	// }
	//
	// @Override
	// protected Boolean willProcessInUiThread(List<Event> events)
	// throws RepositoryException {
	// // unfiltered for the time being
	// return true;
	// }
	//
	// protected void onEventInUiThread(List<Event> events)
	// throws RepositoryException {
	// List<Node> nodesToRefresh = new ArrayList<Node>();
	//
	// for (Event event : events) {
	// String parPath = JcrUtils.parentPath(event.getPath());
	// if (session.nodeExists(parPath)) {
	// Node node = session.getNode(parPath);
	// if (!nodesToRefresh.contains(node)) {
	// nodesToRefresh.add(node);
	// }
	// }
	// }
	//
	// // Update check nodes
	// for (Node node : nodesToRefresh)
	// jcrRefresh(node);
	// refresh(null);
	// }
	// }

	// class AllResultsObserver extends AsyncUiEventListener {
	//
	// public AllResultsObserver(Display display) {
	// super(display);
	// }
	//
	// @Override
	// protected Boolean willProcessInUiThread(List<Event> events)
	// throws RepositoryException {
	// // unfiltered for the time being
	// return true;
	// }
	//
	// protected void onEventInUiThread(List<Event> events)
	// throws RepositoryException {
	// refresh(null);
	// // if (lastSelectedSourceElementParent != null)
	// // refresh(lastSelectedSourceElementParent);
	// }
	// }

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		try {
			session = repository.login();
		} catch (RepositoryException e) {
			throw new AkbException("unable to log in for " + ID + " view");
		}
	}
}