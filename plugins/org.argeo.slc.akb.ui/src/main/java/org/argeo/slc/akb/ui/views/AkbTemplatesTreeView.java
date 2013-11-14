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
import org.argeo.slc.akb.ui.utils.Refreshable;
import org.argeo.slc.akb.ui.views.AkbTemplatesTreeView.ViewDoubleClickListener;
import org.eclipse.jface.action.IContributionItem;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

/** SLC generic JCR Result tree view. */
public class AkbTemplatesTreeView extends ViewPart implements Refreshable {
	// private final static Log log =
	// LogFactory.getLog(AkbTemplatesTreeView.class);

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".akbTemplatesTreeView";

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TreeViewer envTreeViewer;

	// Usefull business objects
	private Node templatesParentNode;

	// Observer
	private EventListener akbNodesObserver = null;
	private final static String[] observedNodeTypes = {
			AkbTypes.AKB_ENV_TEMPLATE, AkbTypes.AKB_CONNECTOR_ALIAS,
			AkbTypes.AKB_ITEM, AkbTypes.AKB_ITEM_FOLDER,
			AkbTypes.AKB_CONNECTOR_FOLDER };

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

		envTreeViewer = createResultsTreeViewer(parent);
		envTreeViewer.setInput(initializeResultTree());

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

			akbNodesObserver = new AkbNodesObserver(viewer.getTree()
					.getDisplay());
			observationManager.addEventListener(akbNodesObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED,
					templatesParentNode.getPath(), true, null,
					observedNodeTypes, false);
		} catch (RepositoryException e) {
			throw new AkbException("Cannot register listeners", e);
		}
		return viewer;
	}

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

	@Override
	public void forceRefresh(Object object) {
		envTreeViewer.setInput(initializeResultTree());
	}

	// ///////////////////////////
	// CONTEXT MENU MANAGEMENT

	/**
	 * Defines the commands that will pop up in the context menu.
	 **/
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = AkbUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		try {

			// Build conditions
			IStructuredSelection selection = (IStructuredSelection) envTreeViewer
					.getSelection();

			Node selected = (Node) selection.getFirstElement();

			boolean hasSelection = selected != null;
			boolean isTemplate = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ENV_TEMPLATE) : false;
			boolean isParentItemsFolder = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ITEM_FOLDER) : false;
			// boolean isParentConnectorsFolder = hasSelection ? selected
			// .isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER) : false;
			boolean isDeletable = hasSelection ? true : false;

			// Add Connector Alias
			Map<String, String> params = new HashMap<String, String>();
			if (hasSelection && isTemplate)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_CONNECTOR_ALIAS);

			// Connector Alias submenu
			refreshAliasesSubmenu(menuManager, window, "menu.aliasesSubmenu",
					"Add Connector Alias", isTemplate, params);

			// Item Submenu
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			refreshItemsSubmenu(menuManager, window, "menu.itemsSubmenu",
					"Add Item", isParentItemsFolder || isTemplate, params);

			// Add Item Folder
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_ITEM_FOLDER);
			AkbUiUtils.refreshParameterizedCommand(menuManager, window,
					"cmd.addItemFolder", OpenAkbNodeEditor.ID,
					"Add item folder", null, isParentItemsFolder || isTemplate,
					params);

			// Delete Item
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(DeleteAkbNodes.PARAM_NODE_JCR_ID,
						selected.getIdentifier());
			AkbUiUtils.refreshParameterizedCommand(menuManager, window,
					"cmd.deleteItem", DeleteAkbNodes.ID,
					"Delete selected item(s)", null, isDeletable, params);

			// create template
			params = new HashMap<String, String>();
			params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
					templatesParentNode.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_ENV_TEMPLATE);
			AkbUiUtils.refreshParameterizedCommand(menuManager, window,
					"cmd.createTemplate", OpenAkbNodeEditor.ID,
					"Create new template...", null,
					!hasSelection || isTemplate, params);

		} catch (RepositoryException re) {
			throw new AkbException("Error while refreshing context menu", re);
		}
	}

	/**
	 * 
	 * refreshes submenu with various connector types
	 * 
	 * @param menuManager
	 * @param locator
	 * @param itemId
	 * @param label
	 * @param isVisible
	 * @param params
	 */
	private void refreshItemsSubmenu(IMenuManager menuManager,
			IServiceLocator locator, String itemId, String label,
			boolean isVisible, Map<String, String> params) {

		// clean
		IContributionItem ici = menuManager.find(itemId);
		if (ici != null)
			menuManager.remove(ici);

		MenuManager subMenu = new MenuManager(label, itemId);

		// JDBC Query
		Map<String, String> tmpParams = new HashMap<String, String>();
		tmpParams.putAll(params);
		tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
				AkbTypes.AKB_JDBC_QUERY);
		String currItemId = "cmd.createJDBCQuery";
		IContributionItem currItem = subMenu.find(currItemId);
		if (currItem != null)
			subMenu.remove(currItem);
		subMenu.add(AkbUiUtils.createContributionItem(menuManager, locator,
				currItemId, OpenAkbNodeEditor.ID, "JDBC", null, tmpParams));

		// SSH COMMAND
		tmpParams = new HashMap<String, String>();
		tmpParams.putAll(params);
		tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
				AkbTypes.AKB_SSH_COMMAND);
		currItemId = "cmd.createSSHCommand";
		currItem = subMenu.find(currItemId);
		if (currItem != null)
			subMenu.remove(currItem);
		subMenu.add(AkbUiUtils.createContributionItem(menuManager, locator,
				currItemId, OpenAkbNodeEditor.ID, "SSH Command", null,
				tmpParams));

		// SSH FILE
		tmpParams = new HashMap<String, String>();
		tmpParams.putAll(params);
		tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_TYPE, AkbTypes.AKB_SSH_FILE);
		currItemId = "cmd.createSSHFile";
		currItem = subMenu.find(currItemId);
		if (currItem != null)
			subMenu.remove(currItem);
		subMenu.add(AkbUiUtils.createContributionItem(menuManager, locator,
				currItemId, OpenAkbNodeEditor.ID, "SSH File", null, tmpParams));

		// refresh
		menuManager.add(subMenu);
		subMenu.setVisible(isVisible);
	}

	/**
	 * 
	 * refreshes submenu with various connector types
	 * 
	 * @param menuManager
	 * @param locator
	 * @param itemId
	 * @param label
	 * @param isVisible
	 * @param params
	 */
	private void refreshAliasesSubmenu(IMenuManager menuManager,
			IServiceLocator locator, String itemId, String label,
			boolean isVisible, Map<String, String> params) {

		// clean
		IContributionItem ici = menuManager.find(itemId);
		if (ici != null)
			menuManager.remove(ici);

		// TODO use dynamic contribution to dynamically retrieve specific
		// connector types
		// CompoundContributionItem comConI = new MyCompoundCI(menuManager,
		// locator, itemId);
		MenuManager subMenu = new MenuManager(label, itemId);

		// JDBC
		Map<String, String> tmpParams = new HashMap<String, String>();
		tmpParams.putAll(params);
		tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_SUBTYPE,
				AkbTypes.AKB_JDBC_CONNECTOR);
		String currItemId = "cmd.createJDBCAlias";
		IContributionItem currItem = subMenu.find(currItemId);
		if (currItem != null)
			subMenu.remove(currItem);
		subMenu.add(AkbUiUtils.createContributionItem(menuManager, locator,
				currItemId, OpenAkbNodeEditor.ID, "JDBC", null, tmpParams));

		// SSH
		tmpParams = new HashMap<String, String>();
		tmpParams.putAll(params);
		tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_SUBTYPE,
				AkbTypes.AKB_SSH_CONNECTOR);
		currItemId = "cmd.createSSHAlias";
		currItem = subMenu.find(currItemId);
		if (currItem != null)
			subMenu.remove(currItem);
		subMenu.add(AkbUiUtils.createContributionItem(menuManager, locator,
				currItemId, OpenAkbNodeEditor.ID, "SSH", null, tmpParams));

		// refresh
		menuManager.add(subMenu);
		subMenu.setVisible(isVisible);
	}

	// private class MyCompoundCI extends CompoundContributionItem {
	// private IMenuManager menuManager;
	// private IServiceLocator locator;
	//
	// public MyCompoundCI(IMenuManager menuManager, IServiceLocator locator,
	// String itemId) {
	// super(itemId);
	// this.menuManager = menuManager;
	// this.locator = locator;
	// }
	//
	// @Override
	// protected IContributionItem[] getContributionItems() {
	//
	// CommandContributionItem[] submenu = new CommandContributionItem[2];
	// submenu[0] = createContributionItem(menuManager, locator, "uid.1",
	// OpenAkbNodeEditor.ID, "test1" + System.currentTimeMillis(),
	// null, null);
	// submenu[1] = createContributionItem(menuManager, locator, "uid.2",
	// OpenAkbNodeEditor.ID, "test2", null, null);
	// return submenu;
	// }
	// }

	/* INNER CLASSES */
	private class AkbNodesObserver extends AsyncUiEventListener {

		public AkbNodesObserver(Display display) {
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

			Object[] visibles = envTreeViewer.getExpandedElements();
			if (fullRefresh)
				envTreeViewer.setInput(initializeResultTree());
			else
				envTreeViewer.refresh();

			envTreeViewer.setExpandedElements(visibles);
		}
	}

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