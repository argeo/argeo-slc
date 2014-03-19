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
import javax.jcr.security.Privilege;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.ArgeoMonitor;
import org.argeo.eclipse.ui.EclipseArgeoMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.PrivilegedJob;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.JavaRepoManager;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Create a copy of the chosen workspace in the local Java repository using a
 * JavaRepoManager.
 */
public class CopyLocalJavaWorkspace extends AbstractHandler {
	private static final Log log = LogFactory
			.getLog(CopyLocalJavaWorkspace.class);

	public final static String ID = DistPlugin.ID + ".copyLocalJavaWorkspace";
	public final static String DEFAULT_LABEL = "Copy Java Workspace...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	public final static String PARAM_SOURCE_WORKSPACE_NAME = "srcWkspName";

	// DEPENDENCY INJECTION
	private Repository nodeRepository;
	private JavaRepoManager javaRepoManager;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String wkspName = event.getParameter(PARAM_SOURCE_WORKSPACE_NAME);

		InputDialog inputDialog = new InputDialog(HandlerUtil
				.getActiveWorkbenchWindow(event).getShell(),
				"New copy of workspace " + wkspName,
				"Choose a name for the workspace to create", "", null);
		int result = inputDialog.open();
		if (result == Window.OK) {
			String newWorkspaceName = inputDialog.getValue();

			if (newWorkspaceName == null || newWorkspaceName.trim().equals("")
					|| newWorkspaceName.trim().equals(wkspName.trim())) {
				ErrorDialog
						.openError(HandlerUtil.getActiveShell(event),
								"Non valid workspace name", newWorkspaceName
										+ " is not a valid workspace name.",
								new Status(IStatus.ERROR, "not valid", 0,
										"Error", null));
				return null;
			}
			Job copyWkspJob = new CopyWkspJob(javaRepoManager, nodeRepository,
					wkspName, newWorkspaceName, HandlerUtil
							.getActiveWorkbenchWindow(event).getShell()
							.getDisplay());
			copyWkspJob.setUser(true);
			copyWkspJob.schedule();
		}
		return null;
	}

	private static class CopyWkspJob extends PrivilegedJob {

		private JavaRepoManager javaRepoManager;
		private Repository localRepository;
		private String srcWkspName;
		private String targetWkspName;
		private Display display;

		public CopyWkspJob(JavaRepoManager javaRepoManager,
				Repository localRepository, String srcWkspName,
				String targetWkspName, Display display) {
			super("Duplicate workspace");
			this.javaRepoManager = javaRepoManager;
			this.localRepository = localRepository;
			this.srcWkspName = srcWkspName;
			this.targetWkspName = targetWkspName;
			this.display = display;
		}

		@Override
		protected IStatus doRun(IProgressMonitor progressMonitor) {
			long begin = System.currentTimeMillis();

			ArgeoMonitor monitor = new EclipseArgeoMonitor(progressMonitor);
			monitor.beginTask("Copy workspace", -1);
			monitor.subTask("Copying nodes");

			Session srcSession = null;
			Session targetSession = null;
			try {
				// Initialize source
				srcSession = localRepository.login(srcWkspName);
				Node srcRootNode = srcSession.getRootNode();

				// Create the workspace -
				// FIXME will throw an error if workspace already exists
				javaRepoManager.createWorkspace(targetWkspName);
				targetSession = localRepository.login(targetWkspName);
				Node newRootNode = targetSession.getRootNode();

				RepoUtils.copy(srcRootNode, newRootNode);
				targetSession.save();
				JcrUtils.addPrivilege(targetSession, "/",
						SlcConstants.ROLE_SLC, Privilege.JCR_ALL);
				monitor.worked(1);

				display.asyncExec(new Runnable() {
					public void run() {
						CommandHelpers.callCommand(RefreshDistributionsView.ID);
					}
				});

			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Unexpected error while creating the new workspace.",
						re);
			} finally {
				JcrUtils.logoutQuietly(srcSession);
				JcrUtils.logoutQuietly(targetSession);
			}

			monitor.done();
			long duration = (System.currentTimeMillis() - begin) / 1000;// in
																		// s
			if (log.isDebugEnabled())
				log.debug("Duplicated local java workspace " + srcWkspName
						+ " to workspace " + targetWkspName + " in "
						+ (duration / 60) + "min " + (duration % 60) + "s");
			return Status.OK_STATUS;
		}
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setJavaRepoManager(JavaRepoManager javaRepoManager) {
		this.javaRepoManager = javaRepoManager;
	}
}