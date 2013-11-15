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

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.DeleteAkbNodes;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.providers.AkbTreeLabelProvider;
import org.argeo.slc.akb.ui.providers.TemplatesTreeContentProvider;
import org.argeo.slc.akb.ui.utils.Refreshable;
import org.argeo.slc.akb.utils.AkbJcrUtils;
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

/** AKB Active environment tree view. */
public class AkbActiveEnvsTreeView extends ViewPart implements Refreshable {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".akbActiveEnvsTreeView";

	/* DEPENDENCY INJECTION */
	private Session session;
	private AkbService akbService;

	// This page widgets
	private TreeViewer envTreeViewer;

	// Usefull business objects
	private Node activeEnvsParentNode;

	private void initialize() {
		try {
			activeEnvsParentNode = session
					.getNode(AkbNames.AKB_ENVIRONMENTS_BASE_PATH);
		} catch (RepositoryException e) {
			throw new AkbException("unable to initialize AKB Browser view", e);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		initialize();
		envTreeViewer = createTreeViewer(parent);
		envTreeViewer.setInput(initializeTree());
	}

	// The main tree viewer
	protected TreeViewer createTreeViewer(Composite parent) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		int style = SWT.BORDER | SWT.MULTI;

		TreeViewer viewer = new TreeViewer(parent, style);
		viewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new TemplatesTreeContentProvider());
		viewer.setLabelProvider(new AkbTreeLabelProvider());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

		getSite().setSelectionProvider(viewer);

		// context menu
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

		return viewer;
	}

	private Node[] initializeTree() {
		try {
			NodeIterator ni = activeEnvsParentNode.getNodes();
			List<Node> envs = new ArrayList<Node>();
			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (currNode.isNodeType(AkbTypes.AKB_ENV))
					envs.add(currNode);
			}
			Node[] envArr = envs.toArray(new Node[envs.size()]);
			return envArr;
		} catch (RepositoryException re) {
			throw new AkbException("Error while initializing the "
					+ "tree of active environments.", re);
		}
	}

	//////////////////////
	/// LIFE CYCLE
	
	@Override
	public void forceRefresh(Object object) {
		envTreeViewer.setInput(initializeTree());
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(session);
		super.dispose();
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
			Node currEnv = null;

			boolean hasSelection = selected != null;

			if (hasSelection)
				currEnv = AkbJcrUtils.getCurrentTemplate(selected);

			boolean isTemplate = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ENV_TEMPLATE) : false;
			boolean isParentItemsFolder = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ITEM_FOLDER) : false;
			// boolean isParentConnectorsFolder = hasSelection ? selected
			// .isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER) : false;
			boolean isDeletable = hasSelection ? true : false;

			// Add Connector Alias
			Map<String, String> params = new HashMap<String, String>();
			if (hasSelection && isTemplate) {
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
				params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
						currEnv.getIdentifier());
			}
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_CONNECTOR_ALIAS);

			// Connector Alias submenu
			refreshAliasesSubmenu(menuManager, window, "menu.aliasesSubmenu",
					"Add Connector Alias", isTemplate, params);

			// Item Submenu
			params = new HashMap<String, String>();
			if (hasSelection) {
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
				params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
						currEnv.getIdentifier());
			}
			refreshItemsSubmenu(menuManager, window, "menu.itemsSubmenu",
					"Add Item", isParentItemsFolder || isTemplate, params);

			// Add Item Folder
			params = new HashMap<String, String>();
			if (hasSelection) {
				params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
						selected.getIdentifier());
				params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
						currEnv.getIdentifier());
			}
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
					activeEnvsParentNode.getIdentifier());
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

	/* INNER CLASSES */
	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					Node currEnv = AkbJcrUtils.getCurrentTemplate(node);

					// Add Connector Alias
					Map<String, String> params = new HashMap<String, String>();
					params.put(OpenAkbNodeEditor.PARAM_NODE_JCR_ID,
							node.getIdentifier());
					params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
							currEnv.getIdentifier());

					CommandUtils.callCommand(OpenAkbNodeEditor.ID, params);
				}
			} catch (RepositoryException e) {
				throw new AkbException("Cannot open " + obj, e);
			}
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		try {
			session = repository.login();
		} catch (RepositoryException e) {
			throw new AkbException("unable to log in for " + ID + " view");
		}
	}

	public void setAkbService(AkbService akbService) {
		this.akbService = akbService;

	}
}