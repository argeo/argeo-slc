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
package org.argeo.slc.client.ui.dist.views;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.CopyWorkspace;
import org.argeo.slc.client.ui.dist.commands.CreateWorkspace;
import org.argeo.slc.client.ui.dist.commands.DeleteWorkspace;
import org.argeo.slc.client.ui.dist.commands.DisplayRepoInformation;
import org.argeo.slc.client.ui.dist.commands.Fetch;
import org.argeo.slc.client.ui.dist.commands.MergeWorkspaces;
import org.argeo.slc.client.ui.dist.commands.NormalizeDistribution;
import org.argeo.slc.client.ui.dist.commands.NormalizeWorkspace;
import org.argeo.slc.client.ui.dist.commands.PublishWorkspace;
import org.argeo.slc.client.ui.dist.commands.RefreshDistributionsView;
import org.argeo.slc.client.ui.dist.commands.RegisterRepository;
import org.argeo.slc.client.ui.dist.commands.UnregisterRemoteRepo;
import org.argeo.slc.client.ui.dist.controllers.DistTreeComparator;
import org.argeo.slc.client.ui.dist.controllers.DistTreeContentProvider;
import org.argeo.slc.client.ui.dist.controllers.DistTreeDoubleClickListener;
import org.argeo.slc.client.ui.dist.controllers.DistTreeLabelProvider;
import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.WkspGroupElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.jcr.SlcNames;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
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

	public final static String ID = DistPlugin.ID + ".distributionsView";

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
		viewer.addDoubleClickListener(new DistTreeDoubleClickListener());
		viewer.setComparator(new DistTreeComparator());

		// Enable retrieving current tree selected items from outside the view
		getSite().setSelectionProvider(viewer);

		// Drag and drop
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations, tt, new ViewDragListener());
		viewer.addDropSupport(operations, tt, new ViewDropListener(viewer));

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		menuManager.addMenuListener(new IMenuListener() {
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

		try {
			// Most of the implemented commands support only one selected
			// element
			boolean singleElement = ((IStructuredSelection) viewer
					.getSelection()).size() == 1;
			// Get Current selected item :
			Object firstElement = ((IStructuredSelection) viewer.getSelection())
					.getFirstElement();

			if (firstElement instanceof TreeParent
					|| firstElement instanceof DistParentElem) {

				String targetRepoPath = null, workspaceName = null, workspacePrefix = null;
				// String targetRepoUri = null;
				// Build conditions depending on element type
				boolean isDistribElem = false, isRepoElem = false, isDistribGroupElem = false;
				boolean isHomeRepo = false, isReadOnly = true;

				RepoElem re = null;

				if (firstElement instanceof WorkspaceElem) {
					WorkspaceElem de = (WorkspaceElem) firstElement;
					re = de.getRepoElem();
					isDistribElem = true;
					isReadOnly = de.isReadOnly();
					workspaceName = de.getWorkspaceName();
				} else if (firstElement instanceof RepoElem) {
					re = (RepoElem) firstElement;
					isRepoElem = true;
					isHomeRepo = re.inHome();
					isReadOnly = re.isReadOnly();
				} else if (firstElement instanceof WkspGroupElem) {
					WkspGroupElem dge = (WkspGroupElem) firstElement;
					isReadOnly = dge.isReadOnly();
					isDistribGroupElem = true;
					re = dge.getRepoElem();
					workspacePrefix = dge.getLabel();
				}

				if (re != null) {
					// targetRepoUri = re.getUri();
					targetRepoPath = re.getRepoNode().getPath();
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
				CommandUtils.refreshParametrizedCommand(menuManager, window,
						CreateWorkspace.ID, CreateWorkspace.DEFAULT_LABEL,
						CreateWorkspace.DEFAULT_ICON,
						(isRepoElem || isDistribGroupElem) && singleElement
								&& !isReadOnly, params);

				// Register a remote repository
				CommandUtils.refreshCommand(menuManager, window,
						RegisterRepository.ID,
						RegisterRepository.DEFAULT_LABEL,
						RegisterRepository.DEFAULT_ICON, isRepoElem
								&& singleElement);

				// Unregister a remote repository
				params = new HashMap<String, String>();
				params.put(UnregisterRemoteRepo.PARAM_REPO_PATH, targetRepoPath);
				CommandUtils.refreshParametrizedCommand(menuManager, window,
						UnregisterRemoteRepo.ID,
						UnregisterRemoteRepo.DEFAULT_LABEL,
						UnregisterRemoteRepo.DEFAULT_ICON, isRepoElem
								&& !isHomeRepo && singleElement, params);

				// Fetch repository
				params = new HashMap<String, String>();
				params.put(Fetch.PARAM_TARGET_REPO_PATH, targetRepoPath);
				CommandUtils.refreshParametrizedCommand(menuManager, window,
						Fetch.ID, Fetch.DEFAULT_LABEL, Fetch.DEFAULT_ICON,
						isRepoElem && isHomeRepo && singleElement
								&& !isReadOnly, params);

				// Normalize workspace
				params = new HashMap<String, String>();
				params.put(NormalizeWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(NormalizeWorkspace.PARAM_WORKSPACE_NAME,
						workspaceName);

				CommandUtils.refreshParametrizedCommand(menuManager, window,
						NormalizeWorkspace.ID, "Normalize...",
						NormalizeWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Copy workspace
				params = new HashMap<String, String>();
				params.put(CopyWorkspace.PARAM_TARGET_REPO_PATH, targetRepoPath);
				params.put(CopyWorkspace.PARAM_SOURCE_WORKSPACE_NAME,
						workspaceName);
				CommandUtils.refreshParametrizedCommand(menuManager, window,
						CopyWorkspace.ID, CopyWorkspace.DEFAULT_LABEL,
						CopyWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement, params);

				// Clear Workspace
				params = new HashMap<String, String>();
				params.put(DeleteWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(DeleteWorkspace.PARAM_WORKSPACE_NAME, workspaceName);
				CommandUtils.refreshParametrizedCommand(menuManager, window,
						DeleteWorkspace.ID, DeleteWorkspace.DEFAULT_LABEL,
						DeleteWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

				// Advanced submenu
				MenuManager submenu = new MenuManager("Advanced", DistPlugin.ID
						+ ".advancedSubmenu");
				IContributionItem ici = menuManager.find(DistPlugin.ID
						+ ".advancedSubmenu");
				if (ici != null)
					menuManager.remove(ici);

				// Publish workspace
				params = new HashMap<String, String>();
				params.put(PublishWorkspace.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(PublishWorkspace.PARAM_WORKSPACE_NAME, workspaceName);
				CommandUtils.refreshParametrizedCommand(submenu, window,
						PublishWorkspace.ID, PublishWorkspace.DEFAULT_LABEL,
						PublishWorkspace.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);
				
				// Normalize distribution (Legacy)
				params = new HashMap<String, String>();
				params.put(NormalizeDistribution.PARAM_TARGET_REPO_PATH,
						targetRepoPath);
				params.put(NormalizeDistribution.PARAM_WORKSPACE_NAME,
						workspaceName);
				CommandUtils.refreshParametrizedCommand(submenu, window,
						NormalizeDistribution.ID,
						NormalizeDistribution.DEFAULT_LABEL,
						NormalizeDistribution.DEFAULT_ICON, isDistribElem
								&& singleElement && !isReadOnly, params);

			

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
					+ "building context menu", e);
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

	/** Listens to drag */
	class ViewDragListener extends DragSourceAdapter {
		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof WorkspaceElem) {
				WorkspaceElem de = (WorkspaceElem) selection.getFirstElement();
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = de.getRepoElem().getUri() + "/"
							+ de.getWorkspaceName();
				}
			}
		}
	}

	/** Listens to drop */
	class ViewDropListener extends ViewerDropAdapter {

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			WorkspaceElem sourceDist = (WorkspaceElem) getSelectedObject();
			RepoElem targetRepo = (RepoElem) getCurrentTarget();

			Boolean ok = MessageDialog.openConfirm(getSite().getShell(),
					"Confirm distribution merge", "Do you want to merge "
							+ sourceDist.getWorkspaceName() + " (from repo "
							+ sourceDist.getRepoElem().getLabel()
							+ ") to repo " + targetRepo.getLabel() + "?");
			if (!ok)
				return false;

			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put(MergeWorkspaces.PARAM_TARGET_REPO_PATH, targetRepo
						.getRepoNode().getPath());
				params.put(MergeWorkspaces.PARAM_SOURCE_REPO_PATH, sourceDist
						.getRepoElem().getRepoNode().getPath());
				params.put(MergeWorkspaces.PARAM_SOURCE_WORKSPACE_NAME,
						sourceDist.getWorkspaceName());
				CommandHelpers.callCommand(RefreshDistributionsView.ID, params);
				return true;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot process drop from " + sourceDist
						+ " to " + targetRepo, e);
			}
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			if (target instanceof RepoElem) {
				if (getSelectedObject() instanceof WorkspaceElem) {
					// check if not same repository
					String srcRepoUri = ((WorkspaceElem) getSelectedObject())
							.getRepoElem().getUri();
					String targetRepoUri = ((RepoElem) target).getUri();
					return !targetRepoUri.equals(srcRepoUri);
				}
			}
			return false;
		}
	}

	/*
	 * DEPENDENCY INJECTION
	 */
	public void setNodeRepository(Repository repository) {
		this.nodeRepository = repository;
	}

	public void setTreeContentProvider(
			DistTreeContentProvider treeContentProvider) {
		this.treeContentProvider = treeContentProvider;
	}
}