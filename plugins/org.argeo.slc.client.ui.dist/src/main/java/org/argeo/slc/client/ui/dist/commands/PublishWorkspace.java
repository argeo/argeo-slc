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
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.argeo.slc.client.ui.dist.views.DistributionsView.DistributionViewSelectedElement;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Publish the current workspace by giving REOD_ONLY rights to anonymous.
 */

public class PublishWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(PublishWorkspace.class);
	public final static String ID = DistPlugin.ID + ".publishWorkspace";
	public final static String DEFAULT_LABEL = "Publish workspace";
	public final static String DEFAULT_ICON_PATH = "icons/publish.gif";

	private String publicRole = "anonymous";

	private String workspaceName;
	private Repository repository;
	private Credentials credentials;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow iww = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPart view = iww.getActivePage().getActivePart();
		if (view instanceof DistributionsView) {
			DistributionViewSelectedElement dvse = ((DistributionsView) view)
					.getSelectedElement();
			if (dvse != null && dvse.isWorkspace) {
				repository = dvse.repository;
				credentials = dvse.credentials;
				workspaceName = dvse.wkspName;
			}
		}

		if (repository != null && workspaceName != null) {
			String msg = "Are you sure you want to publish this distribution: "
					+ workspaceName + " ?";
			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm publication", msg);

			if (result) {

				Session session = null;
				try {
					session = repository.login(credentials, workspaceName);
					JcrUtils.addPrivilege(session, "/", publicRole,
							Privilege.JCR_READ);
					JcrUtils.logoutQuietly(session);
					// CommandHelpers.callCommand(RefreshDistributionsView.ID);
				} catch (RepositoryException re) {
					throw new ArgeoException(
							"Unexpected error while publishing workspace "
									+ workspaceName, re);
				} finally {
					JcrUtils.logoutQuietly(session);
				}
			}
		}
		return null;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}
}