package org.argeo.slc.ide.ui.launch.osgi;

import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

public class OsgiBootLaunchShortcut extends AbstractOsgiLaunchShortcut {
	protected IFile propertiesFile = null;

	@Override
	protected String getLaunchConfigurationTypeName() {
		return OsgiBootEquinoxLaunchConfiguration.ID;
	}

	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("Launch shortcut... " + selection);

		// we assume that:
		// - only one
		// - file
		// - is selected
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Iterator<?> it = sSelection.iterator();
		propertiesFile = (IFile) it.next();

		name = new StringBuffer(extractName(propertiesFile));

		Properties properties = null;
		try {
			properties = OsgiLaunchHelper.readProperties(propertiesFile);
		} catch (CoreException e) {
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Cannot execute launch shortcut", e.getStatus());
			return;
		}

		OsgiLaunchHelper.interpretProperties(properties, bundlesToStart,
				systemPropertiesToAppend);
		super.launch(selection, mode);
	}

	@Override
	protected void initializeConfiguration(
			ILaunchConfigurationWorkingCopy configuration) {
		IResource[] resources = { propertiesFile };
		configuration.setMappedResources(resources);
		super.initializeConfiguration(configuration);
	}

	@Override
	protected String findWorkingDirectory() {
		try {
			//String relPath = "exec/" + extractName(propertiesFile);

			IProject project = propertiesFile.getProject();
			IPath parent = propertiesFile.getProjectRelativePath()
					.removeLastSegments(1);
			IFolder execFolder = project.getFolder(parent.append("exec"));
			execFolder.create(true, true, null);
			IFolder launchFolder = project.getFolder(execFolder
					.getProjectRelativePath().append(
							extractName(propertiesFile)));
			launchFolder.create(true, true, null);

			// IPath execDirPath = propertiesFile.getFullPath()
			// .removeLastSegments(1).append(relPath);
			// File baseDir = propertiesFile.getRawLocation().toFile()
			// .getParentFile();
			// File execDir = new File(baseDir.getCanonicalPath()
			// + File.separatorChar
			// + relPath.replace('/', File.separatorChar));
			// File execDir = execDirPath.toFile();
			// execDir.mkdirs();
			// return "${workspace_loc:" + execDirPath.toString().substring(1)
			// + "}";
			return "${workspace_loc:"
					+ launchFolder.getFullPath().toString().substring(1) + "}";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected String extractName(IFile file) {
		IPath path = propertiesFile.getFullPath();
		IPath pathNoExt = path.removeFileExtension();
		return pathNoExt.segment(pathNoExt.segmentCount() - 1);

	}
}
