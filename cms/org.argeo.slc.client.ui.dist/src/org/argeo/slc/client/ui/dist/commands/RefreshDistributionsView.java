package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.views.AnonymousDistributionsView;
import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/** Force refresh of the Distributions View */
public class RefreshDistributionsView extends AbstractHandler {
	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".refreshDistributionsView";
	public final static String DEFAULT_LABEL = "Refresh the distribution view";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/refresh.png");

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof DistributionsView)
			((DistributionsView) activePart).refresh();
		else if (activePart instanceof AnonymousDistributionsView)
			((AnonymousDistributionsView) activePart).refresh();
		return null;
	}
}
