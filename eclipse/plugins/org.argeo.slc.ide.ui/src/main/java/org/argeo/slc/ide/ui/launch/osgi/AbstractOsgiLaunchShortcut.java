package org.argeo.slc.ide.ui.launch.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractOsgiLaunchShortcut extends OSGiLaunchShortcut {
	protected StringBuffer name = null;

	protected List<String> bundlesToStart = new ArrayList<String>();
	protected Map<String, String> systemPropertiesToAppend = new HashMap<String, String>();

	@Override
	public void launch(ISelection selection, String mode) {
		super.launch(selection, mode);
		name = null;
		bundlesToStart.clear();
		systemPropertiesToAppend.clear();
	}

	protected void initializeConfiguration(
			ILaunchConfigurationWorkingCopy configuration) {
		try {
			super.initializeConfiguration(configuration);

			configuration
					.setAttribute(
							OsgiLauncherConstants.ATTR_DEFAULT_VM_ARGS,
							configuration
									.getAttribute(
											IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
											""));
//			String defaultProgArgs = configuration.getAttribute(
//					IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
//					"");
//			configuration.setAttribute(
//					OsgiLauncherConstants.ATTR_DEFAULT_PROGRAM_ARGS,
//					defaultProgArgs);

			configuration.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					findWorkingDirectory());
			OsgiLaunchHelper.updateLaunchConfiguration(configuration,
					bundlesToStart, systemPropertiesToAppend, null);
		} catch (CoreException e) {
			Shell shell = Display.getCurrent().getActiveShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot execute initalize configuration", e.getStatus());
		}

	}

	protected String findWorkingDirectory() {
		// Choose working directory
		Shell shell = Display.getCurrent().getActiveShell();
		DirectoryDialog dirDialog = new DirectoryDialog(shell);
		dirDialog.setText("Working Directory");
		dirDialog.setMessage("Choose the working directory");
		return dirDialog.open();
	}

	protected void printVm(String prefix, IVMInstall vmInstall) {
		System.out.println(prefix + " vmInstall: id=" + vmInstall.getId()
				+ ", name=" + vmInstall.getName() + ", installLocation="
				+ vmInstall.getInstallLocation() + ", toString=" + vmInstall);
		if (vmInstall instanceof IVMInstall2) {
			IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
			System.out.println("  vmInstall: javaVersion="
					+ vmInstall2.getJavaVersion());
		}
	}

	protected String getName(ILaunchConfigurationType type) {
		if (name != null && !name.toString().trim().equals(""))
			return name.toString();
		else
			return "SLC";
	}

	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		if (name != null) {
			return name.toString().equals(configuration.getName());
		}
		return super.isGoodMatch(configuration);
	}

}
