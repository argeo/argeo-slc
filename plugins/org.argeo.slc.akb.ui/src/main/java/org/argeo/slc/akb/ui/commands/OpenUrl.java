package org.argeo.slc.akb.ui.commands;

import java.net.URL;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

/**
 * Open various predefine URL on the web to ease end user understanding of the
 * app.
 */
public class OpenUrl extends AbstractHandler {

	public final static String ID = AkbUiPlugin.PLUGIN_ID + ".openUrl";
	public final static String PARAM_URL_TYPE = "param.urlType";

	public final static String PARAM_VALUE_TRACKER = "tracker";
	public final static String PARAM_VALUE_WIKI = "wiki";

	private final static String TRACKER_URL = "https://www.argeo.org/bugzilla/enter_bug.cgi?product=slc&component=akb";
	private final static String WIKI_URL = "https://www.argeo.org/wiki/SLC_Active_Knowledge_Base";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String urlType = event.getParameter(PARAM_URL_TYPE);
		try {
			URL url = null;
			if (PARAM_VALUE_TRACKER.equals(urlType))
				url = new URL(TRACKER_URL);
			else if (PARAM_VALUE_WIKI.equals(urlType))
				url = new URL(WIKI_URL);
			else
				return null;
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(url);
		} catch (Exception e) {
			throw new AkbException("Unable to open browser page", e);
		}
		return null;
	}
}