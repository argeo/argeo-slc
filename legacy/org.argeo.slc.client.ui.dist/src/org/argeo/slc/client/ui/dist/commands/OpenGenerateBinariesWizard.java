package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.GenerateBinariesWizard;
import org.argeo.slc.repo.RepoService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/** Open a {@code GenerateBinariesWizard} wizard for the selected node */
public class OpenGenerateBinariesWizard extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".openGenerateBinariesWizard";
	public final static String DEFAULT_LABEL = "Generate Aether Index";
	public final static ImageDescriptor DEFAULT_ICON = null;

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	// Absolute Coordinates of the current group node
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";
	public final static String PARAM_MODULE_PATH = "param.modulePath";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();

		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);
		String modulePath = event.getParameter(PARAM_MODULE_PATH);

		GenerateBinariesWizard wizard = new GenerateBinariesWizard(repoService,
				repoNodePath, workspaceName, modulePath);

		WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		int result = dialog.open();

		if (result == Dialog.OK
				&& (activePart instanceof RefreshDistributionsView))
			CommandHelpers.callCommand(RefreshDistributionsView.ID);

		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}