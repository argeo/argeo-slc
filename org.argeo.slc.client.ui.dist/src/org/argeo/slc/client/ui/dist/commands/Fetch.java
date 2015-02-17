package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.FetchWizard;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Wrap a {@code RepoSync} as an Eclipse command. Open a wizard that enable
 * definition of the fetch process parameters
 */
public class Fetch extends AbstractHandler {
	// private final static Log log = LogFactory.getLog(Fetch.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".fetch";
	public final static String DEFAULT_LABEL = "Fetch...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/fetchRepo.png");

	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private Keyring keyring;
	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		Session currSession = null;
		try {
			// Target Repository
			String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
			currSession = nodeRepository.login();
			Node targetRepoNode = currSession.getNode(targetRepoPath);

			FetchWizard wizard = new FetchWizard(keyring, repositoryFactory,
					nodeRepository);
			wizard.setTargetRepoNode(targetRepoNode);
			WizardDialog dialog = new WizardDialog(
					HandlerUtil.getActiveShell(event), wizard);

			int result = dialog.open();
			if (result == Dialog.OK)
				CommandHelpers.callCommand(RefreshDistributionsView.ID);
			return null;
		} catch (RepositoryException e) {
			throw new SlcException("Unable te retrieve repo node from path", e);
		} finally {
			JcrUtils.logoutQuietly(currSession);
		}
	}

	// DEPENDENCY INJECTION
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setNodeRepository(Repository repository) {
		this.nodeRepository = repository;
	}
}