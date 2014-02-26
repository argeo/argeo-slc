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
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Create a copy of the chosen workspace in the current repository.
 */

public class CopyWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(CopyWorkspace.class);
	public final static String ID = DistPlugin.ID + ".copyWorkspace";
	public final static String DEFAULT_LABEL = "Duplicate...";
	public final static String PARAM_SOURCE_WORKSPACE_NAME = "srcWkspName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String wkspName = event.getParameter(PARAM_SOURCE_WORKSPACE_NAME);

		Session nodeSession = null;
		Session srcSession = null;
		Session newSession = null;
		try {
			nodeSession = nodeRepository.login();
			Node repoNode = nodeSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, repoNode);

			InputDialog inputDialog = new InputDialog(HandlerUtil
					.getActiveWorkbenchWindow(event).getShell(),
					"New copy of workspace " + wkspName,
					"Choose a name for the workspace to create", "", null);
			int result = inputDialog.open();
			if (result == Window.OK) {
				String newWorkspaceName = inputDialog.getValue();
				srcSession = repository.login(credentials, wkspName);

				// Create the workspace
				srcSession.getWorkspace().createWorkspace(newWorkspaceName);
				Node srcRootNode = srcSession.getRootNode();
				// log in the newly created workspace
				newSession = repository.login(credentials, newWorkspaceName);
				Node newRootNode = newSession.getRootNode();
				RepoUtils.copy(srcRootNode, newRootNode);
				newSession.save();
				JcrUtils.addPrivilege(newSession, "/", SlcConstants.ROLE_SLC,
						Privilege.JCR_ALL);
				CommandHelpers.callCommand(RefreshDistributionsView.ID);
			}
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			JcrUtils.logoutQuietly(newSession);
			JcrUtils.logoutQuietly(srcSession);
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