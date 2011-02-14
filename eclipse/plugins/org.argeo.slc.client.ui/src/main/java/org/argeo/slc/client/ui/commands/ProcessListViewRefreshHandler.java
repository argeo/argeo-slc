package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ProcessListView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ProcessListViewRefreshHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProcessListView pView = (ProcessListView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ProcessListView.ID);
		pView.retrieveResults();
		return null;
	}

}
