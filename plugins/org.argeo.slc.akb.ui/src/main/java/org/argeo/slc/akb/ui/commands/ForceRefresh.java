package org.argeo.slc.akb.ui.commands;

import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.utils.Refreshable;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Force refreshment of the active part if it implements
 * <Code>Refreshable</code> interface.
 */
public class ForceRefresh extends AbstractHandler {

	public final static String ID = AkbUiPlugin.PLUGIN_ID + ".forceRefresh";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart part = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getActivePart();
		if (part instanceof Refreshable)
			((Refreshable) part).forceRefresh(null);
		return null;
	}
}