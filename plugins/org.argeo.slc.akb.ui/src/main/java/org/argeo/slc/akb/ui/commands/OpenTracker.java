package org.argeo.slc.akb.ui.commands;

import java.net.URL;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

/**
 * Open a browser with bugzilla
 */
public class OpenTracker extends AbstractHandler {

	public final static String ID = AkbUiPlugin.PLUGIN_ID + ".forceRefresh";

	private final static String TRACKER_URL = "https://www.argeo.org/bugzilla/enter_bug.cgi?product=slc";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL(TRACKER_URL));
		} catch (Exception e) {
			throw new AkbException("Unable to open browser page", e);
		}
		return null;
	}
}