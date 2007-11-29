package org.argeo.slc.ui.launch.script;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

public class SlcScriptLaunchShortcut implements ILaunchShortcut {

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
			IProject project = file.getProject();
			IPath relativePath = file.getProjectRelativePath();
			String name = "["+project.getName() + "] - " + relativePath.toString();
			name = name.replace('/', '_');// otherwise not properly saved

			System.out.println(name);

			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(SlcScriptLaunchDelegate.ID);
			ILaunchConfigurationWorkingCopy wc = type.newInstance(null, name);
			wc.setMappedResources(new IFile[] { file });
			ILaunchConfiguration config = wc.doSave();
			config.launch(mode, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public void launch(IEditorPart editor, String mode) {
		// not (yet) implemented
	}

}
