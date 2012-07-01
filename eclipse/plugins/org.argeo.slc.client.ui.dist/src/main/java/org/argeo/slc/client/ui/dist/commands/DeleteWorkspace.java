package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Repository;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Delete chosen workspace in the current repository.
 */

public class DeleteWorkspace extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);
	public final static String ID = DistPlugin.ID + ".deleteWorkspace";
	public final static String PARAM_WORKSPACE_NAME = DistPlugin.ID
			+ ".workspaceName";
	public final static String DEFAULT_LABEL = "Delete";
	public final static String DEFAULT_ICON_PATH = "icons/removeItem.gif";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		MessageDialog.openWarning(DistPlugin.getDefault()
				.getWorkbench().getDisplay().getActiveShell(),
				"WARNING", "Not yet implemented");
		return null;
	
//		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);
//		String msg = "Your are about to definitively delete this workspace ["
//				+ workspaceName + "].\n Do you really want to proceed ?";

//		boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
//				.getWorkbench().getDisplay().getActiveShell(),
//				"Confirm deletion", msg);
//		if (result) {
//			Session session = null;
//			try {
//				session = repository.login();
//				session.getWorkspace().deleteWorkspace(workspaceName);
//				CommandHelpers.callCommand(RefreshDistributionsView.ID);
//			} catch (RepositoryException re) {
//				throw new ArgeoException(
//						"Unexpected error while deleting workspace ["
//								+ workspaceName + "].", re);
//			} finally {
//				if (session != null)
//					session.logout();
//			}
//		}
//		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}