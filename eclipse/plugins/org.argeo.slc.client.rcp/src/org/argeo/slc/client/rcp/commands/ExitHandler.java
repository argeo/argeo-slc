package org.argeo.slc.client.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExitHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		HandlerUtil.getActiveWorkbenchWindow(event).close();
		return null;
	}

}
