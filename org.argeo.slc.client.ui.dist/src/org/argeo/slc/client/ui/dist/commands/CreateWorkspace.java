package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.api.security.Keyring;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.handlers.HandlerUtil;

/** Create a new empty workspace in a remote repository */
public class CreateWorkspace extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CreateWorkspace.class);

	// Exposes commands meta-info
	public final static String ID = DistPlugin.PLUGIN_ID + ".createWorkspace";
	public final static String DEFAULT_LABEL = "Create workspace...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	// Parameters
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";
	public final static String PARAM_WORKSPACE_PREFIX = "workspacePrefix";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String prefix = event.getParameter(PARAM_WORKSPACE_PREFIX);

		Session nodeSession = null;
		Session session = null;
		try {
			nodeSession = nodeRepository.login();
			Node repoNode = nodeSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, repoNode);

			// TODO : add an input validator
			InputDialog inputDialog = new InputDialog(HandlerUtil
					.getActiveWorkbenchWindow(event).getShell(),
					"Workspace name?",
					"Choose a name for the workspace to create",
					prefix == null ? "" : prefix + "-", null);
			int result = inputDialog.open();

			String enteredName = inputDialog.getValue();

			final String legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXZY0123456789_";
			char[] arr = enteredName.toUpperCase().toCharArray();
			int count = 0;
			for (int i = 0; i < arr.length; i++) {
				if (legalChars.indexOf(arr[i]) == -1)
					count = count + 7;
				else
					count++;
			}

			if (log.isTraceEnabled())
				log.trace("Translated workspace name length: " + count
						+ " (name: " + enteredName + " )");

			if (count > 60) {
				ErrorFeedback.show("Workspace name '" + enteredName
						+ "' is too long or contains"
						+ " too many special characters such as '.' or '-'.");
				return null;
			}

			String workspaceName = enteredName;

			// Canceled by user
			if (result == Dialog.CANCEL || workspaceName == null
					|| "".equals(workspaceName.trim()))
				return null;

			session = repository.login(credentials);
			session.getWorkspace().createWorkspace(workspaceName);
			JcrUtils.logoutQuietly(session);
			// init new workspace
			session = repository.login(credentials, workspaceName);
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC,
					Privilege.JCR_ALL);
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
			if (log.isTraceEnabled())
				log.trace("WORKSPACE " + workspaceName + " CREATED");

		} catch (RepositoryException re) {
			ErrorFeedback.show(
					"Unexpected error while creating the new workspace.", re);
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