package org.argeo.slc.ide.ui.launch.osgi;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.pde.ui.launcher.EclipseLauncherTabGroup;
import org.eclipse.pde.ui.launcher.MainTab;
import org.eclipse.pde.ui.launcher.OSGiSettingsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

/** Definition of the set of tabs used in Eclipse Boot launch configuration UI. */
public class EclipseBootLauncherTabGroup extends EclipseLauncherTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new OsgiBootMainTab(true),
				new MainTab(),
				// new PluginsTab() {
				// private boolean activating = false;
				//
				// @Override
				// public void performApply(
				// ILaunchConfigurationWorkingCopy config) {
				// super.performApply(config);
				// if (activating) {
				// try {
				// config.doSave();
				// } catch (CoreException e) {
				// e.printStackTrace();
				// }
				// activating = false;
				// }
				// }
				//
				// @Override
				// public void activated(
				// ILaunchConfigurationWorkingCopy workingCopy) {
				// activating = true;
				// }
				// },
				new OSGiSettingsTab(), new EnvironmentTab(), new TracingTab(),
				new CommonTab() };
		setTabs(tabs);
	}

}
