package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.views.ArtifactsBrowser;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Force the refresh of the artifact browser view */
public class RefreshArtifactBrowser extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ArtifactsBrowser view = (ArtifactsBrowser) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ArtifactsBrowser.ID);
		view.refresh(null);
		return null;
	}
}
