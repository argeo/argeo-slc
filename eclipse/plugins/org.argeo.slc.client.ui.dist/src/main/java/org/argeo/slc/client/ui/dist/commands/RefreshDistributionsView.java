package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Force refresh of the DistributionsView
 */

public class RefreshDistributionsView extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(RefreshDistributionsView.class);
	public final static String ID = DistPlugin.ID + ".refreshDistributionsView";
	public final static String DEFAULT_LABEL = "Refresh the distribution view";
	public final static String DEFAULT_ICON_PATH = "icons/refresh.png";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof DistributionsView)
			((DistributionsView) activePart).refresh();
		return null;
	}
}
