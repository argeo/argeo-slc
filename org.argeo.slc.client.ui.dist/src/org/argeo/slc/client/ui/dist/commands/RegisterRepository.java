package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;

import org.argeo.api.security.Keyring;
import org.argeo.cms.ArgeoNames;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.RegisterRepoWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/** Register a remote repository by creating a node in the current local node. */
public class RegisterRepository extends AbstractHandler implements ArgeoNames,
		SlcNames {

	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".registerRepository";
	public final static String DEFAULT_LABEL = "Register a repository...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addRepo.gif");

	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;
	private Keyring keyring;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		RegisterRepoWizard wizard = new RegisterRepoWizard(keyring,
				repositoryFactory, nodeRepository);
		WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		int result = dialog.open();
		if (result == Dialog.OK)
			CommandHelpers.callCommand(RefreshDistributionsView.ID);
		return null;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}
}