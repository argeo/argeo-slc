package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ProcessDetailView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler to set visible or create a ProcessDetailView. UUID of the
 * process is passed via command parameters.
 * 
 * @author bsinou
 * 
 */

public class ProcessDetailsDisplayHandler extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(ProcessDetailsDisplayHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// We pass the UUID of the process we want to display via command
		// parameters.
		String uuid = event
				.getParameter("org.argeo.slc.client.commands.processUuid");

		// mode = 1 : VIEW_ACTIVATE, Show view mode that indicates the view
		// should be made visible and activated. Use of this mode has the same
		// effect as calling showView.
		try {
			ProcessDetailView pView = (ProcessDetailView) HandlerUtil
					.getActiveWorkbenchWindow(event).getActivePage()
					.showView(ProcessDetailView.ID, "UUID-" + uuid, 1);
			pView.setUuid(uuid);
			pView.retrieveResults();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
