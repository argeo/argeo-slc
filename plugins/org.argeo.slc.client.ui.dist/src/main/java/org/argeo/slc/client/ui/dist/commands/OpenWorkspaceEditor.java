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

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.DistributionWorkspaceEditor;
import org.argeo.slc.client.ui.dist.editors.WorkspaceEditorInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open a distribution workspace editor for a given workspace in a repository
 */
public class OpenWorkspaceEditor extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".openWorkspaceEditor";
	public final static String DEFAULT_LABEL = "Open editor";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/distribution_perspective.gif");

	// use local node repo and repository factory to retrieve and log to
	// relevant repository
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	// use URI and repository factory to retrieve and ANONYMOUSLY log in
	// relevant repository
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";

	/* DEPENDENCY INJECTION */
	private Repository localRepository;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);

		Session defaultSession = null;
		if (repoNodePath != null && repoUri == null) {
			try {

				defaultSession = localRepository.login();
				if (defaultSession.nodeExists(repoNodePath)) {
					Node repoNode = defaultSession.getNode(repoNodePath);
					repoUri = repoNode.getProperty(ArgeoNames.ARGEO_URI)
							.getString();
				}
			} catch (RepositoryException e) {
				throw new SlcException("Unexpected error while "
						+ "getting repoNode info for repoNode at path "
						+ repoNodePath, e);
			} finally {
				JcrUtils.logoutQuietly(defaultSession);
			}
		}

		WorkspaceEditorInput wei = new WorkspaceEditorInput(repoNodePath,
				repoUri, workspaceName);
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.openEditor(wei, DistributionWorkspaceEditor.ID);
		} catch (PartInitException e) {
			throw new SlcException("Unexpected error while "
					+ "opening editor for workspace " + workspaceName
					+ " with URI " + repoUri + " and repoNode at path "
					+ repoNodePath, e);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setLocalRepository(Repository localRepository) {
		this.localRepository = localRepository;
	}
}