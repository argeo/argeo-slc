package org.argeo.slc.ui.launch.script;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SlcScriptLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab {

	private IFile script;
	
	public void createControl(Composite parent) {
		Composite body = new Composite(parent,SWT.NONE);
		new Label(parent, SWT.LEAD).setText("Script location");
		if (script != null) {
			new Label(parent, SWT.LEAD).setText(script.getLocation().toFile()
					.getAbsolutePath());
		}
		setControl(body);
	}

	public String getName() {
		return "SLC";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			script = (IFile) configuration.getMappedResources()[0];
		} catch (CoreException e) {
			throw new RuntimeException("Cannot initialize tab", e);
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

}
