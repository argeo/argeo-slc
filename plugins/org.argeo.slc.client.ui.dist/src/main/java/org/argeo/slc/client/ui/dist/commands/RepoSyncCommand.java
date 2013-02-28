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
import org.argeo.slc.repo.RepoSync;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/** Wraps a {@link RepoSync} as an Eclipse command. */
public class RepoSyncCommand extends AbstractHandler {
	// private final static Log log = LogFactory.getLog(RepoSyncCommand.class);

	public final static String ID = DistPlugin.ID + ".repoSyncCommand";
	public final static String PARAM_TARGET_REPO = "targetRepoPath";
	public final static String DEFAULT_LABEL = "Fetch ...";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	// DEPENDENCY INJECTION
	private Keyring keyring;
	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;

	private Session currSession;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			currSession = nodeRepository.login();
			// Target Repository
			String targetRepoPath = event.getParameter(PARAM_TARGET_REPO);
			Node targetRepoNode = currSession.getNode(targetRepoPath);

			FetchWizard wizard = new FetchWizard(keyring, repositoryFactory,
					nodeRepository);
			wizard.setTargetRepoNode(targetRepoNode);

			WizardDialog dialog = new WizardDialog(
					HandlerUtil.getActiveShell(event), wizard);
			dialog.open();
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
			return null;
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while fetching data", e);
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