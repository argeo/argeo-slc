package org.argeo.slc.client.ui.dist.commands;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Delete chosen artifacts from the current workspace.
 */

public class DeleteArtifacts extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);
	public final static String ID = DistPlugin.ID + ".deleteArtifacts";
	public final static String DEFAULT_LABEL = "Delete selected items";
	public final static String DEFAULT_ICON_PATH = "icons/removeItem.gif";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String msg = "Your are about to definitively remove these artifacts.\n"
				+ "Do you really want to proceed ?";

		boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
				.getWorkbench().getDisplay().getActiveShell(),
				"Confirm deletion", msg);
		if (result) {
			// Session session = null;
			try {
				// session = repository.login();
				IWorkbenchPart activePart = DistPlugin.getDefault()
						.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActivePart();

				if (activePart instanceof IEditorPart) {
					ISelection selector = ((IEditorPart) activePart)
							.getEditorSite().getSelectionProvider()
							.getSelection();
					if (selector != null
							&& selector instanceof IStructuredSelection) {
						Iterator<?> it = ((IStructuredSelection) selector)
								.iterator();
						while (it.hasNext()) {
							Node node = (Node) it.next();
							// we remove the artifactVersion, that is the parent
							node.getParent().remove();
							node.getSession().save();
						}
					}
					// session.save();
				}
				CommandHelpers.callCommand(RefreshDistributionOverviewPage.ID);
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Unexpected error while deleting artifacts.", re);
			} finally {
				// if (session != null)
				// session.logout();
			}
		}
		return null;
	}
}
