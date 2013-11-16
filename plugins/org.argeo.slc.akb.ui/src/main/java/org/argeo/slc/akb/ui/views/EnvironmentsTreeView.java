package org.argeo.slc.akb.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.CreateAkbNode;
import org.argeo.slc.akb.ui.commands.DeleteAkbNodes;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.providers.ActiveEnvsTreeContentProvider;
import org.argeo.slc.akb.ui.providers.ActiveTreeItem;
import org.argeo.slc.akb.ui.providers.AkbTreeLabelProvider;
import org.argeo.slc.akb.ui.utils.Refreshable;
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

/** AKB Active environment tree view. */
public class EnvironmentsTreeView extends ViewPart implements Refreshable {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".environmentsTreeView";

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
		envTreeViewer.setInput(activeEnvsParentNode);
	}

	// The main tree viewer
	protected TreeViewer createTreeViewer(Composite parent) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		int style = SWT.BORDER | SWT.MULTI;

		TreeViewer viewer = new TreeViewer(parent, style);
		viewer.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new ActiveEnvsTreeContentProvider());
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

	// ////////////////////
	// / LIFE CYCLE

	@Override
	public void forceRefresh(Object object) {
		envTreeViewer.setInput(activeEnvsParentNode);
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

			ActiveTreeItem item = (ActiveTreeItem) selection.getFirstElement();

			boolean hasSelection = item != null;
			Node selected = null, currEnv = null;

			if (hasSelection) {
				selected = item.getNode();
				currEnv = item.getEnvironment();
			}
			boolean isEnv = hasSelection ? selected
					.isNodeType(AkbTypes.AKB_ENV) : false;
			boolean isDeletable = hasSelection ? isEnv : false;
			Map<String, String> params = new HashMap<String, String>();

			// Delete Item
			params = new HashMap<String, String>();
			if (hasSelection)
				params.put(DeleteAkbNodes.PARAM_NODE_JCR_ID,
						selected.getIdentifier());
			AkbUiUtils.refreshParameterizedCommand(menuManager, window,
					"cmd.deleteItem", DeleteAkbNodes.ID,
					"Delete selected active environment", null, isDeletable,
					params);

			// create template
			params = new HashMap<String, String>();
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE, AkbTypes.AKB_ENV);
			AkbUiUtils.refreshParameterizedCommand(menuManager, window,
					"cmd.instanciateEnv", CreateAkbNode.ID,
					"Create new environment instance", null, !hasSelection
							|| isEnv, params);

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
				if (obj instanceof ActiveTreeItem) {
					ActiveTreeItem currItem = (ActiveTreeItem) obj;
					Node node = currItem.getNode();
					Node currEnv = currItem.getEnvironment();
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