package org.argeo.slc.client.ui.dist.commands;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/** Delete chosen artifacts from the current workspace */
public class DeleteArtifacts extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".deleteArtifacts";
	public final static String DEFAULT_LABEL = "Delete selected items";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/removeItem.gif");

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPart activePart = HandlerUtil.getActivePart(event);

			if (activePart instanceof IEditorPart) {
				ISelection selector = ((IEditorPart) activePart)
						.getEditorSite().getSelectionProvider().getSelection();
				if (selector != null
						&& selector instanceof IStructuredSelection) {
					Iterator<?> it = ((IStructuredSelection) selector)
							.iterator();

					String msg = "Your are about to definitively remove the "
							+ ((IStructuredSelection) selector).size()
							+ " selected artifacts.\n"
							+ "Are you sure you want to proceed?";

					boolean result = MessageDialog.openConfirm(DistPlugin
							.getDefault().getWorkbench().getDisplay()
							.getActiveShell(), "Confirm Deletion", msg);

					if (result) {
						while (it.hasNext()) {
							Node node = (Node) it.next();
							if (node.isNodeType(SlcTypes.SLC_ARTIFACT)) {
								// we remove the artifactVersion, that is the
								// parent
								node.getParent().remove();
								node.getSession().save();
							}
						}
					}
				}
			}
			// CommandHelpers.callCommand(RefreshDistributionOverviewPage.ID);
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while deleting artifacts.", re);
		}
		return null;
	}
}