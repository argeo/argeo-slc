package org.argeo.slc.ide.ui.launch.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
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

		IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
		propertiesFile.getProject().refreshLocal(IResource.DEPTH_INFINITE,
				monitor);
	}

	@Override
	protected void preLaunchCheck(ILaunchConfiguration configuration,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// System.out.println("Launching... " + launch);
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

		File workingDir = getWorkingDirectory(configuration);

		ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();

		// Reinitialize using standard OSGi launch shortcut
		// Kind of a hack but it lacks extension capabilities and it is still
		// cleaner than forking the code (which would imply a lot of fork indeed
		// because of all the internal classes)
		new OSGiLaunchShortcut() {
			@Override
			public void initializeConfiguration(
					ILaunchConfigurationWorkingCopy configuration) {
				// TODO Auto-generated method stub
				super.initializeConfiguration(configuration);
			}
		}.initializeConfiguration(wc);

		OsgiLaunchHelper.updateLaunchConfiguration(wc, bundlesToStart,
				systemPropertiesToAppend, null, new File(workingDir, "data")
						.getAbsolutePath());
		wc.doSave();

		super.preLaunchCheck(configuration, launch, monitor);
	}

}
