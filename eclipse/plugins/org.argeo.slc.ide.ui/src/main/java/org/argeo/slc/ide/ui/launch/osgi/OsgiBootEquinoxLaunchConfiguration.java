package org.argeo.slc.ide.ui.launch.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.swt.widgets.Display;

public class OsgiBootEquinoxLaunchConfiguration extends
		EquinoxLaunchConfiguration {
	public final static String ID = SlcIdeUiPlugin.ID
			+ ".OsgiBootEquinoxLauncher";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);

		// TODO: add launch listener to be notified when is terminated and
		// refresh resources
		
		// IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
		// propertiesFile.getProject().refreshLocal(IResource.DEPTH_INFINITE,
		// monitor);
	}

	@Override
	protected void preLaunchCheck(ILaunchConfiguration configuration,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		System.out.println("Launching... " + launch);
		IFile propertiesFile = (IFile) configuration.getMappedResources()[0];

		Properties properties = null;
		try {
			properties = OsgiLaunchHelper.readProperties(propertiesFile);
		} catch (CoreException e) {
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Cannot execute launch shortcut", e.getStatus());
			return;
		}

		List<String> bundlesToStart = new ArrayList<String>();
		Map<String, String> systemPropertiesToAppend = new HashMap<String, String>();
		OsgiLaunchHelper.interpretProperties(properties, bundlesToStart,
				systemPropertiesToAppend);
		ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
		OsgiLaunchHelper.updateLaunchConfiguration(wc, bundlesToStart,
				systemPropertiesToAppend, null);
		wc.doSave();

		super.preLaunchCheck(configuration, launch, monitor);
	}

}
