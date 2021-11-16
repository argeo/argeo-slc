package org.argeo.slc.client.ui.commands;

import java.util.Iterator;

import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.views.JcrResultTreeView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Force refresh the ResultTreeView. This command is only intended to be called
 * by either the toolbar menu of the view or by the popup menu. Refresh due to
 * data changes must be triggered by Observers
 */
public class RefreshJcrResultTreeView extends AbstractHandler {
	public final static String ID = ClientUiPlugin.ID
			+ ".refreshJcrResultTreeView";
	public final static String PARAM_REFRESH_TYPE = ClientUiPlugin.ID
			+ ".param.refreshType";
	public final static String PARAM_REFRESH_TYPE_FULL = "fullRefresh";
	public final static ImageDescriptor DEFAULT_IMG_DESCRIPTOR = ClientUiPlugin
	.getImageDescriptor("icons/refresh.png");
	public final static String DEFAULT_LABEL = "Refresh selected";

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		String refreshType = event.getParameter(PARAM_REFRESH_TYPE);
		JcrResultTreeView view = (JcrResultTreeView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.getActivePart();

		// force full refresh without preserving selection from the tool bar
		if (PARAM_REFRESH_TYPE_FULL.equals(refreshType))
			view.refresh(null);
		else {
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil
					.getActiveWorkbenchWindow(event).getActivePage()
					.getSelection();
			@SuppressWarnings("rawtypes")
			Iterator it = selection.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof ResultParent) {
					view.refresh((ResultParent) obj);
				}
			}
		}
		return null;
	}
}
