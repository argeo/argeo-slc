package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Create a copy of the chosen workspace in the current repository.
 */

public class CopyWorkspace extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CopyWorkspace.class);
	public final static String ID = DistPlugin.ID + ".copyWorkspace";
	public final static String PARAM_WORKSPACE_NAME = DistPlugin.ID
			+ ".workspaceName";
	public final static String DEFAULT_LABEL = "Duplicate";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String srcWorkspaceName = event.getParameter(PARAM_WORKSPACE_NAME);

		if (log.isTraceEnabled())
			log.debug("WORKSPACE " + srcWorkspaceName + " About to be copied");

		// MessageDialog.openWarning(DistPlugin.getDefault()
		// .getWorkbench().getDisplay().getActiveShell(),
		// "WARNING", "Not yet implemented");
		// return null;

		IWorkbenchWindow iww = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		InputDialog inputDialog = new InputDialog(iww.getShell(),
				"New copy of the current workspace",
				"Choose a name for the workspace to create", "", null);
		inputDialog.open();
		String newWorkspaceName = inputDialog.getValue();
		Session srcSession = null;
		Session newSession = null;
		try {
			srcSession = repository.login(srcWorkspaceName);

			// Create the workspace
			srcSession.getWorkspace().createWorkspace(newWorkspaceName);
			Node srcRootNode = srcSession.getRootNode();
			// log in the newly created workspace
			newSession = repository.login(newWorkspaceName);
			// newSession.save();
			Node newRootNode = newSession.getRootNode();
			RepoUtils.copy(srcRootNode, newRootNode);
			newSession.save();

			CommandHelpers.callCommand(RefreshDistributionsView.ID);
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			if (srcSession != null)
				srcSession.logout();
			if (newSession != null)
				newSession.logout();
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
