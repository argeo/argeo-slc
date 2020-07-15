package org.argeo.cms.ui.workbench.internal.useradmin.commands;

import org.argeo.cms.ui.workbench.internal.useradmin.UserAdminWrapper;
import org.argeo.cms.ui.workbench.internal.useradmin.parts.UserBatchUpdateWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/** Launch a wizard to perform batch process on users */
public class UserBatchUpdate extends AbstractHandler {

	/* DEPENDENCY INJECTION */
	private UserAdminWrapper uaWrapper;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		UserBatchUpdateWizard wizard = new UserBatchUpdateWizard(uaWrapper);
		wizard.setWindowTitle("User batch processing");
		WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

	public void setUserAdminWrapper(UserAdminWrapper userAdminWrapper) {
		this.uaWrapper = userAdminWrapper;
	}
}
