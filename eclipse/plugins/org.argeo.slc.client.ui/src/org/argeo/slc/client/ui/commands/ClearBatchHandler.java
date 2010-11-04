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
 *         Remove all processes from the batch built in the ProcessBuilderView
 * 
 *         NOTE : only one batch is supported with this command, if more than
 *         one batch is planned, this class must be updated with parameter.
 */
public class ClearBatchHandler extends AbstractHandler {
	// private final static Log log =
	// LogFactory.getLog(ClearBatchHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProcessBuilderView pbView = (ProcessBuilderView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ProcessBuilderView.ID);
		pbView.clearBatch();
		return null;
	}

}
