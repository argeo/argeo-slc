package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.argeo.api.security.Keyring;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

/** Publish the current workspace by giving READ_ONLY rights to anonymous */
public class PublishWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(PublishWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".publishWorkspace";
	public final static String DEFAULT_LABEL = "Make Public";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/publish.gif");

	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	private String publicRole = SlcConstants.USER_ANONYMOUS;

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
			throw new SlcException(
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