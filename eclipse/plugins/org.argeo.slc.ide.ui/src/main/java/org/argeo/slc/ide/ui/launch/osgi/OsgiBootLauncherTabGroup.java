package org.argeo.slc.ide.ui.launch.osgi;

import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.ui.launcher.OSGiLauncherTabGroup;
import org.eclipse.pde.ui.launcher.OSGiSettingsTab;

public class OsgiBootLauncherTabGroup extends OSGiLauncherTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JavaArgumentsTab(), new OSGiSettingsTab() };
		setTabs(tabs);
	}

}
