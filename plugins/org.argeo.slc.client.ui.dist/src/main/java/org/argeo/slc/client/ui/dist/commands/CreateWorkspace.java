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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Create a new empty workspace in the current repository.
 */

public class CreateWorkspace extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CreateWorkspace.class);
	public final static String ID = DistPlugin.ID + ".createWorkspace";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";
	public final static String PARAM_WORKSPACE_PREFIX = "workspacePrefix";
	public final static String DEFAULT_LABEL = "Create workspace...";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	private String slcRole = "ROLE_SLC";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String prefix = event.getParameter(PARAM_WORKSPACE_PREFIX);

		Session nodeSession = null;
		Session session = null;
		try {
			nodeSession = nodeRepository.login();
			Node repoNode = nodeSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, repoNode);

			// TODO : add an input validator
			InputDialog inputDialog = new InputDialog(HandlerUtil
					.getActiveWorkbenchWindow(event).getShell(),
					"Workspace name?",
					"Choose a name for the workspace to create",
					prefix == null ? "" : prefix + "-", null);
			int result = inputDialog.open();

			String workspaceName = inputDialog.getValue();

			// Canceled by user
			if (result == Dialog.CANCEL || workspaceName == null
					|| "".equals(workspaceName.trim()))
				return null;

			session = repository.login(credentials);
			session.getWorkspace().createWorkspace(workspaceName);
			JcrUtils.logoutQuietly(session);
			// init new workspace
			session = repository.login(credentials, workspaceName);
			JcrUtils.addPrivilege(session, "/", slcRole, Privilege.JCR_ALL);
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
			if (log.isTraceEnabled())
				log.trace("WORKSPACE " + workspaceName + " CREATED");

		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
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