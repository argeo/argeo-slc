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
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.argeo.slc.client.ui.dist.views.DistributionsView.DistributionViewSelectedElement;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Create a copy of the chosen workspace in the current repository.
 */

public class CopyWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(CopyWorkspace.class);
	public final static String ID = DistPlugin.ID + ".copyWorkspace";
	public final static String DEFAULT_LABEL = "Duplicate";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	private Repository repository;
	private Credentials credentials;
	private String wkspName;
	private String slcRole = "ROLE_SLC";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow iww = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPart view = iww.getActivePage().getActivePart();
		if (view instanceof DistributionsView) {
			DistributionViewSelectedElement dvse = ((DistributionsView) view)
					.getSelectedElement();
			if (dvse != null && (dvse.isWorkspace)) {
				repository = dvse.repository;
				credentials = dvse.credentials;
				wkspName = dvse.wkspName;
			}
		}
		if (repository == null || wkspName == null)
			return null;

		InputDialog inputDialog = new InputDialog(iww.getShell(),
				"New copy of workspace " + wkspName,
				"Choose a name for the workspace to create", "", null);
		inputDialog.open();
		String newWorkspaceName = inputDialog.getValue();
		Session srcSession = null;
		Session newSession = null;
		try {
			srcSession = repository.login(credentials, wkspName);

			// Create the workspace
			srcSession.getWorkspace().createWorkspace(newWorkspaceName);
			Node srcRootNode = srcSession.getRootNode();
			// log in the newly created workspace
			newSession = repository.login(credentials, newWorkspaceName);
			Node newRootNode = newSession.getRootNode();
			RepoUtils.copy(srcRootNode, newRootNode);
			newSession.save();
			JcrUtils.addPrivilege(newSession, "/", slcRole, Privilege.JCR_ALL);
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			if (srcSession != null)
				srcSession.logout();
			if (newSession != null)
				newSession.logout();
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
