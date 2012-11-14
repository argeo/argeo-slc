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

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Create a new empty workspace in the current repository.
 */

public class CreateWorkspace extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CreateWorkspace.class);
	public final static String ID = DistPlugin.ID + ".createWorkspace";
	public final static String DEFAULT_LABEL = "Create new workspace";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow iww = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		// TODO : add an input validator
		InputDialog inputDialog = new InputDialog(iww.getShell(),
				"New workspace", "Choose a name for the workspace to create",
				"", null);
		inputDialog.open();
		String workspaceName = inputDialog.getValue();
		Session session = null;
		try {
			session = repository.login();
			session.getWorkspace().createWorkspace(workspaceName);
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			if (session != null)
				session.logout();
		}
		if (log.isTraceEnabled())
			log.debug("WORKSPACE " + workspaceName + " CREATED");
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}