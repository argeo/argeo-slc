package org.argeo.slc.ide.ui.launch.osgi;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OsgiBootLaunchShortcut extends OSGiLaunchShortcut implements
		OsgiLauncherConstants {
	private StringBuffer name = null;
	private IFile propertiesFile = null;

	@Override
	protected String getLaunchConfigurationTypeName() {
		return OsgiBootEquinoxLaunchConfiguration.ID;
	}

	@Override
	public void launch(ISelection selection, String mode) {
		// System.out.println("Launch shortcut... " + selection);

		// we assume that:
		// - only one
		// - file
		// - is selected
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Iterator<?> it = sSelection.iterator();
		propertiesFile = (IFile) it.next();

		name = new StringBuffer(extractName(propertiesFile));

		super.launch(selection, mode);
	}

	@Override
	protected void initializeConfiguration(
			ILaunchConfigurationWorkingCopy configuration) {
		IResource[] resources = { propertiesFile };
		configuration.setMappedResources(resources);
		super.initializeConfiguration(configuration);

		try {
			configuration.setAttribute(ATTR_ADD_JVM_PATHS, false);
			configuration.setAttribute(ATTR_ADDITIONAL_VM_ARGS, "-Xmx128m");
			configuration
					.setAttribute(ATTR_ADDITIONAL_PROGRAM_ARGS, "-console");

			// Defaults
			String originalVmArgs = configuration.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
			configuration.setAttribute(ATTR_DEFAULT_VM_ARGS, originalVmArgs);
			configuration.setAttribute(IPDELauncherConstants.CONFIG_CLEAR_AREA,
					true);

			configuration.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					findWorkingDirectory());

			OsgiLaunchHelper.updateLaunchConfiguration(configuration);
		} catch (CoreException e) {
			Shell shell = Display.getCurrent().getActiveShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot execute initalize configuration", e.getStatus());
		}
	}

	protected String findWorkingDirectory() {
		try {
			IProject project = propertiesFile.getProject();
			IPath parent = propertiesFile.getProjectRelativePath()
					.removeLastSegments(1);
			IFolder execFolder = project.getFolder(parent.append("exec"));
			if (!execFolder.exists())
				execFolder.create(true, true, null);
			IFolder launchFolder = project.getFolder(execFolder
					.getProjectRelativePath().append(
							extractName(propertiesFile)));
			if (!launchFolder.exists())
				launchFolder.create(true, true, null);
			return "${workspace_loc:"
					+ launchFolder.getFullPath().toString().substring(1) + "}";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create working directory", e);
		}
	}

	protected String extractName(IFile file) {
		IPath path = propertiesFile.getFullPath();
		IPath pathNoExt = path.removeFileExtension();
		return pathNoExt.segment(pathNoExt.segmentCount() - 1);

	}

	protected String getName(ILaunchConfigurationType type) {
		if (name != null && !name.toString().trim().equals(""))
			return DebugPlugin.getDefault().getLaunchManager()
					.generateUniqueLaunchConfigurationNameFrom(name.toString());
		else
			return DebugPlugin.getDefault().getLaunchManager()
					.generateUniqueLaunchConfigurationNameFrom("SLC");
	}

	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		if (name != null) {
			return name.toString().equals(configuration.getName());
		}
		return super.isGoodMatch(configuration);
	}

}
