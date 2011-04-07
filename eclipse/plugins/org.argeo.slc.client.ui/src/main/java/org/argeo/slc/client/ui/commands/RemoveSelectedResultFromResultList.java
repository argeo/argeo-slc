package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ResultListView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 * @author bsinou
 * 
 *         Launch the batch built in the ProcessBuilderView
 * 
 *         NOTE : only one batch is supported with this command, if more than
 *         one batch is planned, this class must be updated with parameter.
 */

public class RemoveSelectedResultFromResultList extends AbstractHandler {
	public final static String ID = "org.argeo.slc.client.ui.removeSelectedResultFromResultList";

	// private final static Log log =
	// LogFactory.getLog(RemoveSelectedProcessFromBatchHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ResultListView rlView = (ResultListView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ResultListView.ID);
		rlView.removeSelected();
		return null;
	}

}
