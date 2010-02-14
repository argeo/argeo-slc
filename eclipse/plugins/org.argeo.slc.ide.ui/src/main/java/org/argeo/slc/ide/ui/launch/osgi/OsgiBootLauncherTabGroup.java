package org.argeo.slc.ide.ui.launch.osgi;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.ui.launcher.BundlesTab;
import org.eclipse.pde.ui.launcher.OSGiLauncherTabGroup;
import org.eclipse.pde.ui.launcher.OSGiSettingsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

public class OsgiBootLauncherTabGroup extends OSGiLauncherTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JavaArgumentsTab(), new EnvironmentTab(), new BundlesTab(),
				new OSGiSettingsTab(), new TracingTab(), new CommonTab() };
		setTabs(tabs);
	}

}
