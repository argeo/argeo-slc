package org.argeo.cms.ui.workbench.internal.jcr.commands;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

/** Change isSorted state of the DataExplorer Browser */
public class SortChildNodes extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".sortChildNodes";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		JcrBrowserView view = (JcrBrowserView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(JcrBrowserView.ID);

		ICommandService service = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		Command command = service.getCommand(ID);
		State state = command.getState(ID + ".toggleState");

		boolean wasSorted = (Boolean) state.getValue();
		view.setSortChildNodes(!wasSorted);
		state.setValue(!wasSorted);
		return null;
	}
}
