package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.views.ArtifactsBrowser;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Change visible state of the ArtifactBrower size column
 */
public class ShowSizeColumn extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".showSizeColumn";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ArtifactsBrowser view = (ArtifactsBrowser) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ArtifactsBrowser.ID);

		ICommandService service = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		Command command = service.getCommand(ID);
		State state = command.getState(ID + ".toggleState");
	
		boolean wasVisible = (Boolean) state.getValue();
		view.setSizeVisible(!wasVisible);
		state.setValue(!wasVisible);
		return null;
	}
}
