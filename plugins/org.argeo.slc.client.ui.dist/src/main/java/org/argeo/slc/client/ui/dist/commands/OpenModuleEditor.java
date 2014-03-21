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
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.RepoService;
import org.argeo.slc.client.ui.dist.editors.ArtifactVersionEditor;
import org.argeo.slc.client.ui.dist.editors.ModularDistVersionEditor;
import org.argeo.slc.client.ui.dist.editors.ModuleEditorInput;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open the relevant editor for a given module node of a given repository
 * workspace. For the time being, modules can be artifacts or
 * modularDistributions
 */
public class OpenModuleEditor extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".openModuleEditor";
	public final static String DEFAULT_LABEL = "Open relevant editor";

	// use local node repo and repository factory to retrieve and log to
	// relevant repository
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	// use URI and repository factory to retrieve and ANONYMOUSLY log in
	// relevant repository
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";
	public final static String PARAM_MODULE_PATH = "param.modulePath";

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);
		String modulePath = event.getParameter(PARAM_MODULE_PATH);

		Session businessSession = null;
		try {
			businessSession = repoService.getRemoteSession(repoNodePath,
					repoUri, workspaceName);

			Node module = businessSession.getNode(modulePath);
			ModuleEditorInput mei = new ModuleEditorInput(repoNodePath,
					repoUri, workspaceName, modulePath);

			// Choose correct editor based on its mixin
			if (module.isNodeType(SlcTypes.SLC_MODULAR_DISTRIBUTION))
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
						.openEditor(mei, ModularDistVersionEditor.ID);
			else
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
						.openEditor(mei, ArtifactVersionEditor.ID);
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while "
					+ "getting repoNode info for repoNode at path "
					+ repoNodePath, e);
		} catch (PartInitException e) {
			throw new SlcException("Unexpected error while "
					+ "opening editor for workspace " + workspaceName
					+ " with URI " + repoUri + " and repoNode at path "
					+ repoNodePath, e);
		} finally {
			JcrUtils.logoutQuietly(businessSession);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}