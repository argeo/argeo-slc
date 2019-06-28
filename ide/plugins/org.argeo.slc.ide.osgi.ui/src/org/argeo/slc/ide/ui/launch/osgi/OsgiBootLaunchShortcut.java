package org.argeo.slc.ide.ui.launch.osgi;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;

/** Launch shortcut simplifying the launch of a pure OSGi runtime */
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
		// we assume that only one file is selected
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Iterator<?> it = sSelection.iterator();
		propertiesFile = (IFile) it.next();

		name = new StringBuffer(OsgiLaunchHelper.extractName(propertiesFile));

		super.launch(selection, mode);
	}

	@Override
	protected void initializeConfiguration(ILaunchConfigurationWorkingCopy wc) {
		IResource[] resources = { propertiesFile };
		wc.setMappedResources(resources);
		super.initializeConfiguration(wc);

		OsgiLaunchHelper.setDefaults(wc, true);

		wc.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				OsgiLaunchHelper.findWorkingDirectory(propertiesFile));

		OsgiLaunchHelper.updateLaunchConfiguration(wc, false);
	}

	protected String getName(ILaunchConfigurationType type) {
		if (name != null && !name.toString().trim().equals(""))
			return DebugPlugin.getDefault().getLaunchManager()
					.generateLaunchConfigurationName(name.toString());
		else
			return DebugPlugin.getDefault().getLaunchManager()
					.generateLaunchConfigurationName("SLC");
	}

	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		if (name != null) {
			return name.toString().equals(configuration.getName());
		}
		return super.isGoodMatch(configuration);
	}

}
