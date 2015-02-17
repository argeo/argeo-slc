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
import javax.jcr.RepositoryFactory;

import org.argeo.jcr.ArgeoNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.RegisterRepoWizard;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.util.security.Keyring;
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