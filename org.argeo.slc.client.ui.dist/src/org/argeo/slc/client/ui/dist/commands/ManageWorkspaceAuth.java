package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.wizards.ChangeRightsWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/** Open a dialog to manage rights on the current workspace's root node */
public class ManageWorkspaceAuth extends AbstractHandler {
	// private static final Log log =
	// LogFactory.getLog(ManageWorkspaceAuth.class);
	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".manageWorkspaceAuth";
	public final static String DEFAULT_LABEL = "Manage Rights";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/changeRights.gif");

	public final static String PARAM_WORKSPACE_NAME = DistPlugin.PLUGIN_ID
			+ ".workspaceName";

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
			throw new SlcException("Cannot log in the repository "
					+ repository + " in workspace " + workspaceName, re);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}