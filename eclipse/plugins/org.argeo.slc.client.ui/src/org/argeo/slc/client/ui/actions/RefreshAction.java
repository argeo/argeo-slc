package org.argeo.slc.client.ui.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.client.ui.views.ResultListView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class RefreshAction implements IWorkbenchWindowActionDelegate {
	private final static Log log = LogFactory.getLog(RefreshAction.class);
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public RefreshAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		// ExecutionModulesView view = (ExecutionModulesView) window
		// .getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// .findView(ExecutionModulesView.ID);
		// view.getViewer().refresh();
		// MessageDialog.openInformation(window.getShell(), "Ui", "Refreshed");
		log.info("command");
		ResultListView view = (ResultListView) window.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						ResultListView.ID);
		view.retrieveResults();
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}