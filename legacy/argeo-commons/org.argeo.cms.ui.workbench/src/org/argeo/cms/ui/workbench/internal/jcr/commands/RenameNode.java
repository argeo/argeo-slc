package org.argeo.cms.ui.workbench.internal.jcr.commands;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.cms.ui.jcr.model.SingleJcrNodeElem;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.argeo.jcr.JcrUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Canonically call JCR Session#move(String, String) on the first element
 * returned by HandlerUtil#getActiveWorkbenchWindow()
 * (...getActivePage().getSelection()), if it is a {@link SingleJcrNodeElem}.
 * The user must then fill a new name in and confirm
 */
public class RenameNode extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".renameNode";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage iwp = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		ISelection selection = iwp.getSelection();
		if (selection == null || !(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> lst = ((IStructuredSelection) selection).iterator();
		if (lst.hasNext()) {
			Object element = lst.next();
			if (element instanceof SingleJcrNodeElem) {
				SingleJcrNodeElem sjn = (SingleJcrNodeElem) element;
				Node node = sjn.getNode();
				Session session = null;
				String newName = null;
				String oldPath = null;
				try {
					newName = SingleValue.ask("New node name",
							"Please provide a new name for [" + node.getName() + "]");
					// TODO sanity check and user feedback
					newName = JcrUtils.replaceInvalidChars(newName);
					oldPath = node.getPath();
					session = node.getSession();
					session.move(oldPath, JcrUtils.parentPath(oldPath) + "/" + newName);
					session.save();

					// Manually refresh the browser view. Must be enhanced
					if (iwp.getActivePart() instanceof JcrBrowserView)
						((JcrBrowserView) iwp.getActivePart()).refresh(sjn);
				} catch (RepositoryException e) {
					throw new EclipseUiException("Unable to rename " + node + " to " + newName, e);
				}
			}
		}
		return null;
	}
}
