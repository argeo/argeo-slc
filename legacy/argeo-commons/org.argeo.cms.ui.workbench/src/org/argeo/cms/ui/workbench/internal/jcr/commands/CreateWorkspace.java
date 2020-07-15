package org.argeo.cms.ui.workbench.internal.jcr.commands;

import java.util.Arrays;

import org.argeo.cms.ui.jcr.model.RepositoryElem;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Create a new JCR workspace */
public class CreateWorkspace extends AbstractHandler {

	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".addFolderNode";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();

		JcrBrowserView view = (JcrBrowserView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(HandlerUtil.getActivePartId(event));

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (!(obj instanceof RepositoryElem))
				return null;

			RepositoryElem repositoryNode = (RepositoryElem) obj;
			String workspaceName = SingleValue.ask("Workspace name",
					"Enter workspace name");
			if (workspaceName != null) {
				if (Arrays.asList(repositoryNode.getAccessibleWorkspaceNames())
						.contains(workspaceName)) {
					ErrorFeedback.show("Workspace " + workspaceName
							+ " already exists.");
				} else {
					repositoryNode.createWorkspace(workspaceName);
					view.nodeAdded(repositoryNode);
				}
			}
		} else {
			ErrorFeedback.show("Cannot create workspace");
		}
		return null;
	}
}
