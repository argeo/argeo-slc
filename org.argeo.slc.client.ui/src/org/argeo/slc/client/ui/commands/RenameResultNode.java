package org.argeo.slc.client.ui.commands;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Rename a node of type SlcType.SLC_RESULT_FOLDER by moving it.
 */

public class RenameResultNode extends AbstractHandler {
	public final static String ID = ClientUiPlugin.ID + ".renameResultNode";
	public final static ImageDescriptor DEFAULT_IMG_DESCRIPTOR = ClientUiPlugin
			.getImageDescriptor("icons/rename.png");
	public final static String DEFAULT_LABEL = "Rename result";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		// Sanity check, already done when populating the corresponding popup
		// menu.
		if (selection != null && selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				if (obj instanceof SingleResultNode) {
					SingleResultNode rf = (SingleResultNode) obj;
					Node sourceNode = rf.getNode();
					String folderName = SingleValue.ask("Rename result",
							"Enter a new result name");
					if (folderName != null) {

						if (sourceNode.getParent().hasNode(folderName)) {
							MessageDialog
									.openError(Display.getDefault()
											.getActiveShell(), "Error",
											"Another object with the same name already exists.");
							return null;
						}

						String sourcePath = sourceNode.getPath();
						String targetPath = JcrUtils.parentPath(sourcePath)
								+ "/" + folderName;
						Session session = sourceNode.getSession();
						session.move(sourcePath, targetPath);
						session.getNode(targetPath).setProperty(
								Property.JCR_TITLE, folderName);
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