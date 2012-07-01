package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.wizards.ChangeRightsWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open a dialog to change rights on the root node of the current workspace.
 */

public class ManageWorkspaceAuth extends AbstractHandler {
	// private static final Log log =
	// LogFactory.getLog(ManageWorkspaceAuth.class);
	public final static String ID = DistPlugin.ID + ".manageWorkspaceAuth";
	public final static String PARAM_WORKSPACE_NAME = DistPlugin.ID
			+ ".workspaceName";
	public final static String DEFAULT_LABEL = "Change rights for current workspace";
	public final static String DEFAULT_ICON_PATH = "icons/changeRights.gif";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	private Session session;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);
		try {
			session = repository.login(workspaceName);
			ChangeRightsWizard wizard = new ChangeRightsWizard(session);
			WizardDialog dialog = new WizardDialog(
					HandlerUtil.getActiveShell(event), wizard);
			dialog.open();
			return null;
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			if (session != null)
				session.logout();
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}