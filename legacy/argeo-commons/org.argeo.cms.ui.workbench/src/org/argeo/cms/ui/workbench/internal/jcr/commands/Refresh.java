package org.argeo.cms.ui.workbench.internal.jcr.commands;

import java.util.Iterator;

import org.argeo.cms.ui.jcr.JcrBrowserUtils;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Force the selected objects of the active view to be refreshed doing the
 * following:
 * <ol>
 * <li>The model objects are recomputed</li>
 * <li>the view is refreshed</li>
 * </ol>
 */
public class Refresh extends AbstractHandler {

	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".refresh";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		JcrBrowserView view = (JcrBrowserView) WorkbenchUiPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();//

		ISelection selection = WorkbenchUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getSelection();

		if (selection != null && selection instanceof IStructuredSelection
				&& !selection.isEmpty()) {
			Iterator<?> it = ((IStructuredSelection) selection).iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof TreeParent) {
					TreeParent tp = (TreeParent) obj;
					JcrBrowserUtils.forceRefreshIfNeeded(tp);
					view.refresh(obj);
				}
			}
		} else if (view instanceof JcrBrowserView)
			((JcrBrowserView) view).refresh(null); // force full refresh

		return null;
	}
}
