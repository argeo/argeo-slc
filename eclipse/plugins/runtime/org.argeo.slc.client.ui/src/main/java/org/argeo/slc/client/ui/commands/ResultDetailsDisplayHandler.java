package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ResultDetailView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler to set visible or create a ResultDetailView. UUID of the
 * testResult is passed via command parameters.
 * 
 * @author bsinou
 * 
 */

public class ResultDetailsDisplayHandler extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(ResultDetailsDisplayHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// We pass the UUID of the test result we want to display via command
		// parameters.
		String uuid = event
				.getParameter("org.argeo.slc.client.commands.resultUuid");

		// TODO : remove this.
		// if (uuid == null || "".equals(uuid)) {
		// try {
		// ResultListView pbv = (ResultListView) HandlerUtil
		// .getActiveWorkbenchWindow(event).getActivePage()
		// .showView(ResultListView.ID);
		// uuid = pbv.getSelectedResult()[0];
		// } catch (PartInitException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }

		// mode = 2 : VIEW_VISIBLE, Show view mode that indicates the view
		// should be created or made visible if already created .
		// mode = 1 : VIEW_ACTIVATE, Show view mode that indicates the view
		// should be made visible and activated. Use of this mode has the same
		// effect as calling
		try {
			ResultDetailView rView = (ResultDetailView) HandlerUtil
					.getActiveWorkbenchWindow(event).getActivePage()
					.showView(ResultDetailView.ID, "UUID-" + uuid, 1);
			rView.setUuid(uuid);
			rView.retrieveResults();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
