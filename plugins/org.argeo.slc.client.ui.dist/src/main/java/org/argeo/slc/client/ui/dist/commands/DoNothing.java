package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Utilitary command used to enable sub menus in various toolbars. Does nothing
 */
public class DoNothing extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".doNothing";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}
}
