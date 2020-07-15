package org.argeo.cms.ui.workbench.commands;

import org.argeo.cms.ui.workbench.UserHomePerspective;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Default action of the user menu */
public class OpenHomePerspective extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		try {
			HandlerUtil.getActiveSite(event).getWorkbenchWindow()
					.openPage(UserHomePerspective.ID, null);
		} catch (WorkbenchException e) {
			ErrorFeedback.show("Cannot open home perspective", e);
		}
		return null;
	}
}
