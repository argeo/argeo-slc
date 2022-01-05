package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.api.cms.CmsLog;
import org.argeo.cms.security.Keyring;
import org.argeo.cms.ui.workbench.util.PrivilegedJob;
import org.argeo.eclipse.ui.jcr.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
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

/** Create a copy of the chosen workspace in a remote repository */
public class CopyWorkspace extends AbstractHandler {
	private static final CmsLog log = CmsLog.getLog(CopyWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".copyWorkspace";
	public final static String DEFAULT_LABEL = "Duplicate...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	public final static String PARAM_SOURCE_WORKSPACE_NAME = "srcWkspName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
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
			Job copyWkspJob = new CopyWkspJob(repositoryFactory, keyring,
					nodeRepository, targetRepoPath, wkspName, newWorkspaceName,
					HandlerUtil.getActiveWorkbenchWindow(event).getShell()
							.getDisplay());
			copyWkspJob.setUser(true);
			copyWkspJob.schedule();
		}
		return null;
	}

	private static class CopyWkspJob extends PrivilegedJob {

		private RepositoryFactory repositoryFactory;
		private Keyring keyring;
		private Repository localRepository;
		private String targetRepoPath;
		private String srcWkspName;
		private String targetWkspName;
		private Display display;

		public CopyWkspJob(RepositoryFactory repositoryFactory,
				Keyring keyring, Repository localRepository,
				String targetRepoPath, String srcWkspName,
				String targetWkspName, Display display) {
			super("Duplicate workspace");
			this.repositoryFactory = repositoryFactory;
			this.keyring = keyring;
			this.localRepository = localRepository;
			this.targetRepoPath = targetRepoPath;
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

			Session nodeSession = null;
			Session srcSession = null;
			Session newSession = null;
			try {
				nodeSession = localRepository.login();
				Node repoNode = nodeSession.getNode(targetRepoPath);
				Repository repository = RepoUtils.getRepository(
						repositoryFactory, keyring, repoNode);
				Credentials credentials = RepoUtils.getRepositoryCredentials(
						keyring, repoNode);

				srcSession = repository.login(credentials, srcWkspName);

				// Create the workspace
				srcSession.getWorkspace().createWorkspace(targetWkspName);
				Node srcRootNode = srcSession.getRootNode();
				// log in the newly created workspace
				newSession = repository.login(credentials, targetWkspName);
				Node newRootNode = newSession.getRootNode();
				RepoUtils.copy(srcRootNode, newRootNode, monitor);
				newSession.save();
				JcrUtils.addPrivilege(newSession, "/", SlcConstants.ROLE_SLC,
						Privilege.JCR_ALL);

				display.asyncExec(new Runnable() {
					public void run() {
						CommandHelpers.callCommand(RefreshDistributionsView.ID);
					}
				});
				monitor.worked(1);

			} catch (RepositoryException re) {
				throw new SlcException(
						"Unexpected error while creating the new workspace.",
						re);
			} finally {
				JcrUtils.logoutQuietly(newSession);
				JcrUtils.logoutQuietly(srcSession);
				JcrUtils.logoutQuietly(nodeSession);
			}

			monitor.done();
			long duration = (System.currentTimeMillis() - begin) / 1000;// in
																		// s
			if (log.isDebugEnabled())
				log.debug("Created workspace " + targetWkspName + " in "
						+ (duration / 60) + "min " + (duration % 60) + "s");
			return Status.OK_STATUS;
		}

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