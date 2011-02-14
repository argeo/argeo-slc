package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ProcessBuilderView;
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

public class RemoveSelectedProcessFromBatchHandler extends AbstractHandler {
	// private final static Log log =
	// LogFactory.getLog(RemoveSelectedProcessFromBatchHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProcessBuilderView pbView = (ProcessBuilderView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ProcessBuilderView.ID);
		pbView.removeSelected();
		return null;
	}

}
