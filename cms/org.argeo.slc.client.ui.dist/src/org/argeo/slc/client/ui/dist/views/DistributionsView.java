package org.argeo.slc.client.ui.dist.views;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.argeo.cms.ArgeoNames;
import org.argeo.cms.ui.workbench.util.CommandUtils;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.CopyLocalJavaWorkspace;
import org.argeo.slc.client.ui.dist.commands.CopyWorkspace;
import org.argeo.slc.client.ui.dist.commands.CreateLocalJavaWorkspace;
import org.argeo.slc.client.ui.dist.commands.CreateWorkspace;
import org.argeo.slc.client.ui.dist.commands.DeleteWorkspace;
import org.argeo.slc.client.ui.dist.commands.DisplayRepoInformation;
import org.argeo.slc.client.ui.dist.commands.Fetch;
import org.argeo.slc.client.ui.dist.commands.NormalizeDistribution;
import org.argeo.slc.client.ui.dist.commands.NormalizeWorkspace;
import org.argeo.slc.client.ui.dist.commands.OpenGenerateBinariesWizard;
import org.argeo.slc.client.ui.dist.commands.PublishWorkspace;
import org.argeo.slc.client.ui.dist.commands.RegisterRepository;
import org.argeo.slc.client.ui.dist.commands.RunInOsgi;
import org.argeo.slc.client.ui.dist.commands.UnregisterRemoteRepo;
import org.argeo.slc.client.ui.dist.controllers.DistTreeComparator;
import org.argeo.slc.client.ui.dist.controllers.DistTreeComparer;
import org.argeo.slc.client.ui.dist.controllers.DistTreeContentProvider;
import org.argeo.slc.client.ui.dist.controllers.DistTreeDoubleClickListener;
import org.argeo.slc.client.ui.dist.controllers.DistTreeLabelProvider;
import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionBaseElem;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WkspGroupElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

/**
 * Browse, manipulate and manage distributions across multiple repositories
 * (like fetch, merge, publish, etc.).
 */
public class DistributionsView extends ViewPart implements SlcNames, ArgeoNames {
	// private final static Log log =
	// LogFactory.getLog(DistributionsView.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".distributionsView";

	/* DEPENDENCY INJECTION */
	private Repository nodeRepository;
	private DistTreeContentProvider treeContentProvider;

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		// Define the TableViewer
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(400);
		col.setLabelProvider(new DistTreeLabelProvider());

		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);

		// viewer.setContentProvider(new DistTreeContentProvider());
		viewer.setContentProvider(treeContentProvider);
		viewer.addDoubleClickListener(new DistTreeDoubleClickListener(viewer));
		viewer.setComparer(new DistTreeComparer());
		viewer.setComparator(new DistTreeComparator());

		@SuppressWarnings("unused")
		ViewerComparator vc = viewer.getComparator();

		// Enable retrieving current tree selected items from outside the view
		getSite().setSelectionProvider(viewer);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		menuManager.addMenuListener(new IMenuListener() {
			private static final long serialVersionUID = -1454108001335038652L;

			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		// Initialize
		refresh();
	}

	/** Programatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();

		// Most of the implemented commands support only one selected
		// element
		boolean singleElement = ((IStructuredSelection) viewer.getSelection())
				.size() == 1;
		// Get Current selected item :
		Object firstElement = ((IStructuredSelection) viewer.getSelection())
				.getFirstElement();

		try {

			if (firstElement instanceof TreeParent
					|| firstElement instanceof DistParentElem) {

				String targetRepoPath = null, workspaceName = null, workspacePrefix = null;
				String modularDistBasePath = null;
				String modularDistPath = null;
				// String targetRepoUri = null;
				// Build conditions depending on element type
				boolean isDistribElem = false, isModularDistVersionBaseElem = false, isRepoElem = false, isDistribGroupElem = false;
				boolean isLocal = false, isReadOnly = true;

				RepoElem re = null;

				if (firstElement instanceof RepoElem) {
					re = (RepoElem) firstElement;
					isRepoElem = true;
					isLocal = re.inHome();
					isReadOnly = re.isReadOnly();
				} else if (firstElement instanceof WkspGroupElem) {
					WkspGroupElem wge = (WkspGroupElem) firstElement;
					isReadOnly = wge.isReadOnly();
					isDistribGroupElem = true;
					re = (RepoElem) wge.getParent();
					workspacePrefix = wge.getName();
				} else if (firstElement instanceof WorkspaceElem) {
					WorkspaceElem we = (WorkspaceElem) firstElement;
					re = we.getRepoElem();
					isDistribElem = true;
					isReadOnly = we.isReadOnly();
					workspaceName = we.getWorkspaceName();
					isLocal = we.inHome();
				} else if (firstElement instanceof ModularDistVersionBaseElem) {
					ModularDistVersionBaseElem mdbe = (ModularDistVersionBaseElem) firstElement;
					isModularDistVersionBaseElem = true;
					re = mdbe.getWkspElem().getRepoElem();
					isLocal = re.inHome();
					isReadOnly = re.isReadOnly();
					workspaceName = mdbe.getWkspElem().getWorkspaceName();
					modularDistBasePath = mdbe.getModularDistBase().getPath();
				} else if (firstElement instanceof ModularDistVersionElem) {
					ModularDistVersionElem mdbe = (ModularDistVersionElem) firstElement;
					re = mdbe.getWorkspaceElem().getRepoElem();
					isLocal = re.inHome();
					isReadOnly = re.isReadOnly();
					workspaceName = mdbe.getWorkspaceElem().getWorkspaceName();
					modularDistPath = mdbe.getModularDistVersionNode()
							.getPath();
				}

				if (re != null) {
					targetRepoPath = re.getRepoNodePath();
				}

				// Display repo info
				CommandUtils.refreshCommand(menuManager, window,
						DisplayRepoInformation.ID,
						DisplayRepoInformation.DEFAULT_LABEL,
						DisplayRepoInformation.DEFAULT_ICON, isRepoElem
								&& singleElement);

				// create workspace
				Map<String, String> params = new HashMap<String, String>();
				params.put(CreateWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(CreateWorkspace.PARAM_WORKSPACE_PREFIX,
						workspacePrefix);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						CreateWorkspace.ID, CreateWorkspace.DEFAULT_LABEL,
						CreateWorkspace.DEFAULT_ICON,
						(isRepoElem || isDistribGroupElem) && singleElement
								&& !isReadOnly && !isLocal, params);

				// TODO Manage the case where it is not a java workspace
				params = new HashMap<String, String>();
				params.put(CreateLocalJavaWorkspace.PARAM_WORKSPACE_PREFIX,
						workspacePrefix);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						CreateLocalJavaWorkspace.ID,
						CreateLocalJavaWorkspace.DEFAULT_LABEL,
						CreateLocalJavaWorkspace.DEFAULT_ICON,
						(isRepoElem || isDistribGroupElem) && singleElement
								&& !isReadOnly && isLocal, params);

				// Register a remote repository
				CommandUtils.refreshCommand(menuManager, window,
						RegisterRepository.ID,
						RegisterRepository.DEFAULT_LABEL,
						RegisterRepository.DEFAULT_ICON, isRepoElem
								&& singleElement);

				// Unregister a remote repository
				params = new HashMap<String, String>();
				params.put(UnregisterRemoteRepo.PARAM_REPO_PATH, targetRepoPath);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						UnregisterRemoteRepo.ID,
						UnregisterRemoteRepo.DEFAULT_LABEL,
						UnregisterRemoteRepo.DEFAULT_ICON, isRepoElem
								&& !isLocal && singleElement, params);

				// Fetch repository
				params = new HashMap<String, String>();
				params.put(Fetch.PARAM_TARGET_REPO_PATH, targetRepoPath);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						Fetch.ID, Fetch.DEFAULT_LABEL, Fetch.DEFAULT_ICON,
						isRepoElem && isLocal && singleElement && !isReadOnly,
						params);

				// Normalize workspace
				params = new HashMap<String, String>();
				params.put(NormalizeWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(NormalizeWorkspace.PARAM_WORKSPACE_NAME,
						workspaceName);

				CommandUtils.refreshParameterizedCommand(menuManager, window,
						NormalizeWorkspace.ID, "Normalize...",
						NormalizeWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Copy workspace
				params = new HashMap<String, String>();
				params.put(CopyWorkspace.PARAM_TARGET_REPO_PATH, targetRepoPath);
				params.put(CopyWorkspace.PARAM_SOURCE_WORKSPACE_NAME,
						workspaceName);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						CopyWorkspace.ID, CopyWorkspace.DEFAULT_LABEL,
						CopyWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isLocal, params);

				params = new HashMap<String, String>();
				params.put(CopyLocalJavaWorkspace.PARAM_SOURCE_WORKSPACE_NAME,
						workspaceName);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						CopyLocalJavaWorkspace.ID,
						CopyLocalJavaWorkspace.DEFAULT_LABEL,
						CopyLocalJavaWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && isLocal, params);

				// Clear Workspace
				params = new HashMap<String, String>();
				params.put(DeleteWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(DeleteWorkspace.PARAM_WORKSPACE_NAME, workspaceName);
				CommandUtils.refreshParameterizedCommand(menuManager, window,
						DeleteWorkspace.ID, DeleteWorkspace.DEFAULT_LABEL,
						DeleteWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Advanced submenu
				MenuManager submenu = new MenuManager("Advanced",
						DistPlugin.PLUGIN_ID + ".advancedSubmenu");
				IContributionItem ici = menuManager.find(DistPlugin.PLUGIN_ID
						+ ".advancedSubmenu");
				if (ici != null)
					menuManager.remove(ici);

				// Publish workspace
				params = new HashMap<String, String>();
				params.put(PublishWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(PublishWorkspace.PARAM_WORKSPACE_NAME, workspaceName);
				CommandUtils.refreshParameterizedCommand(submenu, window,
						PublishWorkspace.ID, PublishWorkspace.DEFAULT_LABEL,
						PublishWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Normalize distribution (Legacy)
				params = new HashMap<String, String>();
				params.put(NormalizeDistribution.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(NormalizeDistribution.PARAM_WORKSPACE_NAME,
						workspaceName);
				CommandUtils.refreshParameterizedCommand(submenu, window,
						NormalizeDistribution.ID,
						NormalizeDistribution.DEFAULT_LABEL,
						NormalizeDistribution.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Run in OSGi
				params = new HashMap<String, String>();
				params.put(RunInOsgi.PARAM_MODULE_PATH, modularDistPath);
				params.put(RunInOsgi.PARAM_WORKSPACE_NAME, workspaceName);
				CommandUtils.refreshParameterizedCommand(submenu, window,
						RunInOsgi.ID, RunInOsgi.DEFAULT_LABEL,
						RunInOsgi.DEFAULT_ICON, modularDistPath != null
								&& singleElement && isLocal, params);

				// Open generate binaries
				params = new HashMap<String, String>();
				params.put(OpenGenerateBinariesWizard.PARAM_REPO_NODE_PATH,
						targetRepoPath);
				params.put(OpenGenerateBinariesWizard.PARAM_MODULE_PATH,
						modularDistBasePath);
				params.put(OpenGenerateBinariesWizard.PARAM_WORKSPACE_NAME,
						workspaceName);

				CommandUtils.refreshParameterizedCommand(submenu, window,
						OpenGenerateBinariesWizard.ID,
						OpenGenerateBinariesWizard.DEFAULT_LABEL,
						OpenGenerateBinariesWizard.DEFAULT_ICON,
						isModularDistVersionBaseElem && !isReadOnly, params);

				if (submenu.getSize() > 0)
					menuManager.add(submenu);

				// // Manage workspace authorizations
				// params = new HashMap<String, String>();
				// params.put(ManageWorkspaceAuth.PARAM_WORKSPACE_NAME, wsName);
				// CommandHelpers.refreshParameterizedCommand(menuManager,
				// window,
				// ManageWorkspaceAuth.ID, ManageWorkspaceAuth.DEFAULT_LABEL,
				// ManageWorkspaceAuth.DEFAULT_ICON_PATH, isDistribElem
				// && singleElement && !isReadOnly, params);
			}
		} catch (RepositoryException e) {
			throw new SlcException("unexpected errror while "
					+ "building context menu for element " + firstElement, e);
		}
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	/**
	 * Force refresh of the whole view
	 */
	public void refresh() {
		viewer.setInput(nodeRepository);
		viewer.expandToLevel(2);
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setTreeContentProvider(
			DistTreeContentProvider treeContentProvider) {
		this.treeContentProvider = treeContentProvider;
	}
}