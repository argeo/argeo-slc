package org.argeo.cms.ui.workbench.internal.useradmin.commands;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/** Save the currently edited Argeo user. */
public class SaveArgeoUser extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".saveArgeoUser";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPart iwp = HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().getActivePart();
			if (!(iwp instanceof IEditorPart))
				return null;
			IEditorPart editor = (IEditorPart) iwp;
			editor.doSave(null);
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Error", "Cannot save user: " + e.getMessage());
		}
		return null;
	}
}
