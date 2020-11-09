package org.argeo.slc.client.ui.dist.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.repo.JavaRepoManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.handlers.HandlerUtil;

/** Create a new empty workspace in the default local java repository */
public class CreateLocalJavaWorkspace extends AbstractHandler {
	private static final Log log = LogFactory
			.getLog(CreateLocalJavaWorkspace.class);

	// Exposes commands meta-info
	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".createLocalJavaWorkspace";
	public final static String DEFAULT_LABEL = "Create local Java workspace...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");

	// Parameters
	public final static String PARAM_WORKSPACE_PREFIX = "workspacePrefix";

	/* DEPENDENCY INJECTION */
	private JavaRepoManager javaRepoManager;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String prefix = event.getParameter(PARAM_WORKSPACE_PREFIX);
		// TODO : add an input validator
		InputDialog inputDialog = new InputDialog(HandlerUtil
				.getActiveWorkbenchWindow(event).getShell(), "Workspace name?",
				"Choose a name for the workspace to create",
				prefix == null ? "" : prefix + "-", null);
		int result = inputDialog.open();

		String enteredName = inputDialog.getValue();
		final String legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXZY0123456789_";
		char[] arr = enteredName.toUpperCase().toCharArray();
		int count = 0;
		for (int i = 0; i < arr.length; i++) {
			if (legalChars.indexOf(arr[i]) == -1)
				count = count + 7;
			else
				count++;
		}

		if (count > 60) {
			ErrorFeedback.show("Workspace name '" + enteredName
					+ "' is too long or contains"
					+ " too many special characters such as '.' or '-'.");
			return null;
		}

		String workspaceName = enteredName;
		// Canceled by user
		if (result == Dialog.CANCEL || workspaceName == null
				|| "".equals(workspaceName.trim()))
			return null;

		// FIXME will throw an exception if this workspace name is already used.
		javaRepoManager.createWorkspace(workspaceName);

		CommandHelpers.callCommand(RefreshDistributionsView.ID);
		if (log.isTraceEnabled())
			log.trace("WORKSPACE " + workspaceName + " CREATED");

		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setJavaRepoManager(JavaRepoManager javaRepoManager) {
		this.javaRepoManager = javaRepoManager;
	}
}