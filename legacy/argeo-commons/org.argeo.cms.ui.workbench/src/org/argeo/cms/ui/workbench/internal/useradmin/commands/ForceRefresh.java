package org.argeo.cms.ui.workbench.internal.useradmin.commands;

import org.argeo.cms.ui.workbench.internal.useradmin.parts.GroupsView;
import org.argeo.cms.ui.workbench.internal.useradmin.parts.UsersView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/** Retrieve the active view or editor and call forceRefresh method if defined */
public class ForceRefresh extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow iww = HandlerUtil.getActiveWorkbenchWindow(event);
		if (iww == null)
			return null;
		IWorkbenchPage activePage = iww.getActivePage();
		IWorkbenchPart part = activePage.getActivePart();
		if (part instanceof UsersView)
			((UsersView) part).refresh();
		else if (part instanceof GroupsView)
			((GroupsView) part).refresh();
		return null;
	}
}
