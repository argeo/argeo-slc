package org.argeo.slc.ui.launch.script;

import org.argeo.slc.ui.launch.SlcUiLaunchPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

public class SlcScriptLaunchShortcut implements ILaunchShortcut {
	private boolean showDialog = false;

	public void launch(ISelection selection, String mode) {
		try {
			if (!(selection instanceof IStructuredSelection)) {
				throw new RuntimeException("Unknown selection "
						+ selection.getClass());
			}
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() != 1) {
				throw new RuntimeException("Can only launch one SLC script.");
			}
			Object obj = sSelection.iterator().next();
			if (!(obj instanceof IFile)) {
				throw new RuntimeException("Can only launch files.");
			}
			IFile file = ((IFile) obj);

			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(SlcScriptLaunchDelegate.ID);

			// Find or create config
			String configLocation = SlcScriptUtils
					.convertToWorkspaceLocation(file);
			ILaunchConfiguration config = findLaunchConfiguration(
					configLocation, manager.getLaunchConfigurations(type));
			if (config == null) {
				ILaunchConfigurationWorkingCopy wc = type.newInstance(null,
						generateName(file));
				wc.setAttribute(SlcScriptUtils.ATTR_SCRIPT, configLocation);
				wc.setMappedResources(new IFile[] { file });
				config = wc.doSave();
			}

			// Launch
			launch(config, mode);
		} catch (CoreException e) {
			Shell shell = SlcUiLaunchPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot execute SLC launch shortcut", e.getStatus());
		}

	}

	protected String generateName(IFile file) {
		IPath relativePath = file.getProjectRelativePath();
		String name = relativePath.toString();
		int idx = name.lastIndexOf(".xml");
		if (idx > 0)
			name = name.substring(0, idx);

		if (name.startsWith("src/main/slc/root/"))
			name = name.substring("src/main/slc/root/".length());
		else if (name.startsWith("src/main/slc/"))
			name = name.substring("src/main/slc/".length());

		name = name.replace('/', '.');// otherwise not properly saved
		return name;
	}

	protected ILaunchConfiguration findLaunchConfiguration(
			String configLocation, ILaunchConfiguration[] configs)
			throws CoreException {
		for (ILaunchConfiguration config : configs) {
			String loc = config.getAttribute(SlcScriptUtils.ATTR_SCRIPT, "");
			if (loc.equals(configLocation)) {
				return config;
			}
		}
		return null;
	}

	protected void launch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		if (showDialog) {
			IStatus status = new Status(IStatus.INFO, SlcUiLaunchPlugin.ID,
					"Configure SLC Launch");
			String groupId;
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				groupId = IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP;
			} else {
				groupId = IDebugUIConstants.ID_RUN_LAUNCH_GROUP;
			}
			DebugUITools.openLaunchConfigurationDialog(SlcUiLaunchPlugin
					.getDefault().getWorkbench().getActiveWorkbenchWindow()
					.getShell(), configuration, groupId, status);
		} else {
			DebugUITools.launch(configuration, mode);
		}

	}

	public void launch(IEditorPart editor, String mode) {
		// not (yet) implemented
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

}
