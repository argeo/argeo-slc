package org.argeo.slc.ide.ui.launch.osgi;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.launching.EclipseApplicationLaunchConfiguration;

/** OSGiBoot launch configuration. */
public class EclipseBootLaunchConfiguration extends
		EclipseApplicationLaunchConfiguration {
	public final static String ID = SlcIdeUiPlugin.ID + ".EclipseBootLauncher";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);

		// System.out.println("targetBundles="
		// + configuration.getAttribute(
		// IPDELauncherConstants.SELECTED_TARGET_PLUGINS, ""));
		// System.out.println("workspaceBundles="
		// + configuration.getAttribute(
		// IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS, ""));

		// Refresh resources before launching
		final IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
		propertiesFile.getParent().refreshLocal(IResource.DEPTH_INFINITE,
				monitor);
	}

	@Override
	protected void preLaunchCheck(ILaunchConfiguration configuration,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
		OsgiLaunchHelper.updateLaunchConfiguration(wc, true);
		wc.doSave();
		super.preLaunchCheck(configuration, launch, monitor);

		// Note that if a Java project contains a build.properties it has to
		// declare the sources otherwise it will be skipped in the generation of
		// the dev.properties file!

		// for(Object bundleId:fAllBundles.keySet()){
		// System.out.println(bundleId+"="+fAllBundles.get(bundleId));
		// }
	}

}
