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
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Publish the current workspace by giving REOD_ONLY rights to anonymous.
 */

public class PublishWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(PublishWorkspace.class);
	public final static String ID = DistPlugin.ID + ".publishWorkspace";
	public final static String DEFAULT_LABEL = "Publish workspace";
	public final static String DEFAULT_ICON_PATH = "icons/publish.gif";
	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	private String publicRole = "anonymous";

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

			String msg = "Are you sure you want to publish this distribution: "
					+ workspaceName + " ?";
			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm publication", msg);

			if (result) {

				session = repository.login(credentials, workspaceName);
				JcrUtils.addPrivilege(session, "/", publicRole,
						Privilege.JCR_READ);
				session.save();
				JcrUtils.logoutQuietly(session);
				// CommandHelpers.callCommand(RefreshDistributionsView.ID);
			}
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while publishing workspace "
							+ workspaceName, re);
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