package org.argeo.cms.ui.workbench.internal.jcr.commands;

import org.argeo.cms.ui.jcr.model.RemoteRepositoryElem;
import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Remove a registered remote repository */
public class RemoveRemoteRepository extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();

		JcrBrowserView view = (JcrBrowserView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(HandlerUtil.getActivePartId(event));

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof RemoteRepositoryElem) {
				((RemoteRepositoryElem) obj).remove();
				view.refresh(null);
			}
		}
		return null;
	}

}
