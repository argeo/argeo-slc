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
package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Delete chosen workspace in the current repository.
 * 
 * Due to current version of JackRabbit, it only cleans it for the time being,
 * removing all nodes of type {@code NodeType.NT_FOLDER} and
 * {@code NodeType.NT_UNSTRUCTURED}
 */
public class DeleteWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".deleteWorkspace";
	public final static String DEFAULT_LABEL = "Clear";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/removeItem.gif");

	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);

		Session nodeSession = null;
		Session session = null;
		try {
			nodeSession = nodeRepository.login();
			Node repoNode = nodeSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, repoNode);

			String msg = "Your are about to completely delete workspace ["
					+ workspaceName + "].\n Do you really want to proceed?";
			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm workspace deletion", msg);

			if (result) {
				// msg =
				// "There is no possible turning back, are your REALLY sure you want to proceed ?";
				msg = "WARNING: \nCurrent Jackrabbit version used does "
						+ "not support workspace deletion.\n"
						+ "Thus, the workspace will only be cleaned so "
						+ "that you can launch fetch process again.\n\n"
						+ "Do you still want to proceed?";
				result = MessageDialog.openConfirm(DistPlugin.getDefault()
						.getWorkbench().getDisplay().getActiveShell(),
						"Confirm workspace deletion", msg);
			}

			if (result) {
				session = repository.login(credentials, workspaceName);
				// TODO use this with a newer version of Jackrabbit
				// Workspace wsp = session.getWorkspace();
				// wsp.deleteWorkspace(workspaceName);
				NodeIterator nit = session.getRootNode().getNodes();
				while (nit.hasNext()) {
					Node node = nit.nextNode();
					if (node.isNodeType(NodeType.NT_FOLDER)
							|| node.isNodeType(NodeType.NT_UNSTRUCTURED)) {
						// String path = node.getPath();
						node.remove();
						session.save();
					}
				}
				CommandHelpers.callCommand(RefreshDistributionsView.ID);
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while deleting workspace ["
							+ workspaceName + "].", re);
		} finally {
			JcrUtils.logoutQuietly(session);
			JcrUtils.logoutQuietly(nodeSession);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}