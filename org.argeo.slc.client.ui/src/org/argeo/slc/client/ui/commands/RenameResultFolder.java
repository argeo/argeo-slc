package org.argeo.slc.client.ui.commands;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Rename a node of type SlcType.SLC_RESULT_FOLDER by moving it.
 */

public class RenameResultFolder extends AbstractHandler {
	public final static String ID = ClientUiPlugin.ID + ".renameResultFolder";
	public final static ImageDescriptor DEFAULT_IMG_DESCRIPTOR = ClientUiPlugin
			.getImageDescriptor("icons/rename.png");
	public final static String DEFAULT_LABEL = "Rename...";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		// Sanity check, already done when populating the corresponding popup
		// menu.
		if (selection != null && selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				if (obj instanceof ResultFolder) {
					ResultFolder rf = (ResultFolder) obj;
					Node sourceNode = rf.getNode();
					String folderName = SingleValue.ask("Rename folder",
							"Enter a new folder name");
					if (folderName != null) {
						String sourcePath = sourceNode.getPath();
						String targetPath = JcrUtils.parentPath(sourcePath)
								+ "/" + folderName;
						Session session = sourceNode.getSession();
						session.move(sourcePath, targetPath);
						session.save();
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException(
						"Unexpected exception while refactoring result folder",
						e);
			}
		}
		return null;
	}
}