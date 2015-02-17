package org.argeo.slc.client.ui.dist.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.workbench.CommandUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.commands.OpenModuleEditor;
import org.argeo.slc.client.ui.dist.commands.OpenWorkspaceEditor;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/** Listen to double-clicks on the distributions view tree. */
public class DistTreeDoubleClickListener implements IDoubleClickListener {

	private TreeViewer treeViewer;

	public DistTreeDoubleClickListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() == null || event.getSelection().isEmpty())
			return;
		Object obj = ((IStructuredSelection) event.getSelection())
				.getFirstElement();

		if (obj instanceof RepoElem) {
			RepoElem rpNode = (RepoElem) obj;
			if (!rpNode.isConnected()) {
				rpNode.login();
				treeViewer.refresh(obj);
			}
		} else if (obj instanceof WorkspaceElem) {
			WorkspaceElem we = (WorkspaceElem) obj;
			RepoElem repoElem = we.getRepoElem();
			Map<String, String> params = new HashMap<String, String>();
			params.put(OpenWorkspaceEditor.PARAM_REPO_NODE_PATH,
					repoElem.getRepoNodePath());
			params.put(OpenWorkspaceEditor.PARAM_REPO_URI, repoElem.getUri());
			params.put(OpenWorkspaceEditor.PARAM_WORKSPACE_NAME,
					we.getWorkspaceName());
			CommandUtils.callCommand(OpenWorkspaceEditor.ID, params);

		} else if (obj instanceof ModularDistVersionElem) {
			ModularDistVersionElem modDistElem = (ModularDistVersionElem) obj;
			WorkspaceElem wkspElem = modDistElem.getWorkspaceElem();
			Node moduleNode = modDistElem.getModularDistVersionNode();
			RepoElem repoElem = wkspElem.getRepoElem();
			Map<String, String> params = new HashMap<String, String>();
			params.put(OpenModuleEditor.PARAM_REPO_NODE_PATH,
					repoElem.getRepoNodePath());
			params.put(OpenModuleEditor.PARAM_REPO_URI, repoElem.getUri());
			params.put(OpenModuleEditor.PARAM_WORKSPACE_NAME,
					wkspElem.getWorkspaceName());
			try {
				params.put(OpenModuleEditor.PARAM_MODULE_PATH,
						moduleNode.getPath());
			} catch (RepositoryException re) {
				throw new SlcException("Cannot get path for node " + moduleNode
						+ " while setting parameters for "
						+ "command OpenModuleEditor", re);
			}
			CommandUtils.callCommand(OpenModuleEditor.ID, params);
		}
	}
}