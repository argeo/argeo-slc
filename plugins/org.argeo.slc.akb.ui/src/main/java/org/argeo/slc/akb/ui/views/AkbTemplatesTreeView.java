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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.DeleteAkbNodes;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.providers.AkbTreeLabelProvider;
import org.argeo.slc.akb.ui.providers.TemplatesTreeContentProvider;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

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

	// Observer
	private EventListener allResultsObserver = null;
	private final static String[] observedNodeTypesUnderAllResults = {
			AkbTypes.AKB_ENV_TEMPLATE, AkbTypes.AKB_CONNECTOR_ALIAS,
			AkbTypes.AKB_ITEM, AkbTypes.AKB_ITEM_FOLDER,
			AkbTypes.AKB_CONNECTOR_FOLDER };

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

		resultTreeViewer = createResultsTreeViewer(parent);
		resultTreeViewer.setInput(initializeResultTree());

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
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
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

		// Initialize observer
		try {
			ObservationManager observationManager = session.getWorkspace()
					.getObservationManager();

			allResultsObserver = new AllResultsObserver(viewer.getTree()
					.getDisplay());

			// observe tree changes under All results
			observationManager.addEventListener(allResultsObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED,
					templatesParentNode.getPath(), true, null,
					observedNodeTypesUnderAllResults, false);
		} catch (RepositoryException e) {
			throw new AkbException("Cannot register listeners", e);
		}

		// add change listener to display TestResult information in the property
		// viewer
		// viewer.addSelectionChangedListener(new MySelectionChangedListener());
		return viewer;
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
			boolean fullRefresh = false;

			eventLoop: for (Event event : events) {
				String currPath = event.getPath();
				if (session.nodeExists(currPath)) {
					Node node = session.getNode(currPath);
					if (node.isNodeType(AkbTypes.AKB_ENV_TEMPLATE)) {
						fullRefresh = true;
						break eventLoop;
					}
				}
			}

			Object[] visibles = resultTreeViewer.getExpandedElements();
			if (fullRefresh)
				resultTreeViewer.setInput(initializeResultTree());
			else
				resultTreeViewer.refresh();

			resultTreeViewer.setExpandedElements(visibles);
		}
	}

	// Detailed property viewer
	// protected TableViewer createPropertiesViewer(Composite parent) {
	// }

	@Override
	public void setFocus() {
	}

	private Node[] initializeResultTree() {
		try {
			NodeIterator ni = templatesParentNode.getNodes();
			List<Node> templates = new ArrayList<Node>();

			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (currNode.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
					templates.add(currNode);
			}

			Node[] templateArr = templates.toArray(new Node[templates.size()]);
			return templateArr;
		} catch (RepositoryException re) {
			throw new AkbException("Error while initializing templates Tree.",
					re);
		}
	}

	// Manage context menu
	/**
	 * Defines the commands that will pop up in the context menu.
	 **/
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = AkbUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		try {

			// Build conditions
			Node selected = (Node) ((IStructuredSelection) resultTreeViewer
					.getSelection()).getFirstElement();

			boolean hasSelection = selected != null;
			boolean isParentItemsFolder = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ITEM_FOLDER) : false;
			boolean isParentConnectorsFolder = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER) : false;
			boolean isDeletable = hasSelection ? !(selected.getParent()
					.isNodeType(AkbTypes.AKB_ENV_TEMPLATE)) : false;

			// Add Connector Alias
			Map<String, String> params = new HashMap<String, String>();
			if (hasSelection)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_CONNECTOR_ALIAS);
			refreshParameterizedCommand(menuManager, window,
					"cmd.addConnector", OpenAkbNodeEditor.ID,
					"Add connector Alias", null, isParentConnectorsFolder,
					params);

			// Add Item
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE, AkbTypes.AKB_ITEM);
			refreshParameterizedCommand(menuManager, window, "cmd.addItem",
					OpenAkbNodeEditor.ID, "Add item", null,
					isParentItemsFolder, params);

			// Add Item Folder
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_ITEM_FOLDER);
			refreshParameterizedCommand(menuManager, window,
					"cmd.addItemFolder", OpenAkbNodeEditor.ID,
					"Add item folder", null, isParentItemsFolder, params);

			// Delete Item
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(DeleteAkbNodes.PARAM_NODE_JCR_ID,
						selected.getIdentifier());
			refreshParameterizedCommand(menuManager, window, "cmd.deleteItem",
					DeleteAkbNodes.ID, "Delete selected item(s)", null,
					isDeletable, params);

			// create template
			params = new HashMap<String, String>();
			params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
					templatesParentNode.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_ENV_TEMPLATE);
			refreshParameterizedCommand(menuManager, window,
					"cmd.createTemplate", OpenAkbNodeEditor.ID,
					"Create new template...", null, true, params);

		} catch (RepositoryException re) {
			throw new AkbException("Error while refreshing context menu", re);
		}
	}

	/**
	 * Commodities the refresh of a single command with a map of parameters in a
	 * Menu.aboutToShow method to simplify further development
	 * 
	 * @param menuManager
	 * @param locator
	 * @param cmdId
	 * @param label
	 * @param iconPath
	 * @param showCommand
	 */
	private void refreshParameterizedCommand(IMenuManager menuManager,
			IServiceLocator locator, String itemId, String cmdId, String label,
			ImageDescriptor icon, boolean showCommand,
			Map<String, String> params) {
		IContributionItem ici = menuManager.find(itemId);
		if (ici != null)
			menuManager.remove(ici);
		CommandContributionItemParameter contributionItemParameter = new CommandContributionItemParameter(
				locator, itemId, cmdId, SWT.PUSH);

		if (showCommand) {
			// Set Params
			contributionItemParameter.label = label;
			contributionItemParameter.icon = icon;

			if (params != null)
				contributionItemParameter.parameters = params;

			CommandContributionItem cci = new CommandContributionItem(
					contributionItemParameter);
			menuManager.add(cci);
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

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(session);
		super.dispose();
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		try {
			session = repository.login();
		} catch (RepositoryException e) {
			throw new AkbException("unable to log in for " + ID + " view");
		}
	}
}