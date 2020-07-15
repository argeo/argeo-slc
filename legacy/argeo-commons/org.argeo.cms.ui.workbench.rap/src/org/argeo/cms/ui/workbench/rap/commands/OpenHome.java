package org.argeo.cms.ui.workbench.rap.commands;

import org.argeo.cms.ui.workbench.UserHomePerspective;
import org.argeo.cms.ui.workbench.util.CommandUtils;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Default action of the user menu */
public class OpenHome extends AbstractHandler {
	private final static String PROP_OPEN_HOME_CMD_ID = "org.argeo.ui.openHomeCommandId";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String defaultCmdId = System.getProperty(PROP_OPEN_HOME_CMD_ID, "");
		if (!"".equals(defaultCmdId.trim()))
			CommandUtils.callCommand(defaultCmdId);
		else {
			try {
				String defaultPerspective = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench()
						.getPerspectiveRegistry().getDefaultPerspective();
				HandlerUtil.getActiveSite(event).getWorkbenchWindow()
						.openPage(defaultPerspective != null ? defaultPerspective : UserHomePerspective.ID, null);
			} catch (WorkbenchException e) {
				ErrorFeedback.show("Cannot open home perspective", e);
			}
		}
		return null;
	}
}
