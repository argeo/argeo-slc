package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.api.cms.CmsLog;
import org.argeo.cms.ui.workbench.util.PrivilegedJob;
import org.argeo.eclipse.ui.jcr.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
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
	private static final CmsLog log = CmsLog
			.getLog(CopyLocalJavaWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".copyLocalJavaWorkspace";
	public final static String DEFAULT_LABEL = "Copy Java Workspace...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	public final static String PARAM_SOURCE_WORKSPACE_NAME = "srcWkspName";

	// DEPENDENCY INJECTION
	private Repository javaRepository;
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
			Job copyWkspJob = new CopyWkspJob(javaRepoManager, javaRepository,
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
		private Repository javaRepository;
		private String srcWkspName;
		private String targetWkspName;
		private Display display;

		public CopyWkspJob(JavaRepoManager javaRepoManager,
				Repository javaRepository, String srcWkspName,
				String targetWkspName, Display display) {
			super("Duplicate workspace");
			this.javaRepoManager = javaRepoManager;
			this.javaRepository = javaRepository;
			this.srcWkspName = srcWkspName;
			this.targetWkspName = targetWkspName;
			this.display = display;
		}

		@Override
		protected IStatus doRun(IProgressMonitor progressMonitor) {
			long begin = System.currentTimeMillis();

			JcrMonitor monitor = new EclipseJcrMonitor(progressMonitor);
			monitor.beginTask("Copy workspace", -1);
			monitor.subTask("Copying nodes");

			Session srcSession = null;
			Session targetSession = null;
			try {
				// Initialize source
				srcSession = javaRepository.login(srcWkspName);
				Node srcRootNode = srcSession.getRootNode();

				// Create the workspace -
				// FIXME will throw an error if workspace already exists
				javaRepoManager.createWorkspace(targetWkspName);
				targetSession = javaRepository.login(targetWkspName);
				Node newRootNode = targetSession.getRootNode();

				RepoUtils.copy(srcRootNode, newRootNode, monitor);
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
				throw new SlcException(
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
	public void setJavaRepository(Repository javaRepository) {
		this.javaRepository = javaRepository;
	}

	public void setJavaRepoManager(JavaRepoManager javaRepoManager) {
		this.javaRepoManager = javaRepoManager;
	}
}