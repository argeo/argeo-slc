package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.ui.views.ExecutionModulesView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExecutionModulesViewRefreshHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExecutionModulesView eView = (ExecutionModulesView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ExecutionModulesView.ID);
		eView.refreshView();
		return null;
	}

}
