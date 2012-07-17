package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.DistributionEditor;
import org.argeo.slc.client.ui.dist.editors.DistributionOverviewPage;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Force refresh of the Distribution overview page of the corresponding editor
 */

public class RefreshDistributionOverviewPage extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(RefreshDistributionOverviewPage.class);
	public final static String ID = DistPlugin.ID
			+ ".refreshDistributionOverviewPage";
	public final static String DEFAULT_LABEL = "Refresh the distribution overview";
	public final static String DEFAULT_ICON_PATH = "icons/refresh.png";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof DistributionEditor) {
			IFormPage ifp = ((DistributionEditor) activePart)
					.getActivePageInstance();
			if (ifp instanceof DistributionOverviewPage)
				((DistributionOverviewPage) ifp).refresh();
		}
		return null;
	}
}
