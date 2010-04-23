package org.argeo.slc.lib.linux;

import org.argeo.slc.core.deploy.MultiResourceSet;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.deploy.DeploymentData;

public class RedhatDeploymentData implements DeploymentData {
	private ResourceSet configurationFiles;
	private String runlevelsScript;
	private String permissionsScript;

	private RedhatDeploymentData parent;

	public ResourceSet getConfigurationFiles() {
		if (parent != null && parent.getConfigurationFiles() != null) {
			MultiResourceSet mrs = new MultiResourceSet();
			mrs.getResourceSets().add(parent.getConfigurationFiles());
			mrs.getResourceSets().add(configurationFiles);
			return mrs;
		} else {
			return configurationFiles;
		}
	}

	public String getRunlevelsScript() {
		if (parent != null && parent.getRunlevelsScript() != null)
			return parent.getRunlevelsScript() + "\n" + runlevelsScript;
		else
			return runlevelsScript;
	}

	public String getPermissionsScript() {
		if (parent != null && parent.getPermissionsScript() != null)
			return parent.getPermissionsScript() + "\n" + permissionsScript;
		else
			return permissionsScript;
	}

	public void setRunlevelsScript(String runlevelsScript) {
		this.runlevelsScript = runlevelsScript;
	}

	public void setConfigurationFiles(ResourceSet configurationFiles) {
		this.configurationFiles = configurationFiles;
	}

	public void setPermissionsScript(String permissionsScript) {
		this.permissionsScript = permissionsScript;
	}

	public void setParent(RedhatDeploymentData parentDeploymentData) {
		this.parent = parentDeploymentData;
	}

}
