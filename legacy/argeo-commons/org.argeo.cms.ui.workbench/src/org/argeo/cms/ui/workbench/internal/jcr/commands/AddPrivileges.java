package org.argeo.cms.ui.workbench.internal.jcr.commands;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.cms.ui.jcr.model.SingleJcrNodeElem;
import org.argeo.cms.ui.jcr.model.WorkspaceElem;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.jcr.parts.AddPrivilegeWizard;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.useradmin.UserAdmin;

/** Open a dialog to add privileges on the selected node to a chosen group */
public class AddPrivileges extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".addPrivileges";

	/* DEPENDENCY INJECTION */
	private UserAdmin userAdmin;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			TreeParent treeParentNode = null;
			Node jcrParentNode = null;

			if (obj instanceof SingleJcrNodeElem) {
				treeParentNode = (TreeParent) obj;
				jcrParentNode = ((SingleJcrNodeElem) treeParentNode).getNode();
			} else if (obj instanceof WorkspaceElem) {
				treeParentNode = (TreeParent) obj;
				jcrParentNode = ((WorkspaceElem) treeParentNode).getRootNode();
			} else
				return null;

			try {
				String targetPath = jcrParentNode.getPath();
				AddPrivilegeWizard wizard = new AddPrivilegeWizard(
						jcrParentNode.getSession(), targetPath, userAdmin);
				WizardDialog dialog = new WizardDialog(
						HandlerUtil.getActiveShell(event), wizard);
				dialog.open();
				return null;
			} catch (RepositoryException re) {
				throw new EclipseUiException("Unable to retrieve "
						+ "path or JCR session to add privilege on "
						+ jcrParentNode, re);
			}
		} else {
			ErrorFeedback.show("Cannot add privileges");
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setUserAdmin(UserAdmin userAdmin) {
		this.userAdmin = userAdmin;
	}
}
