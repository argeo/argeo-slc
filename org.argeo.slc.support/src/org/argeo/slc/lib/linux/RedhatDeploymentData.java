package org.argeo.slc.lib.linux;

import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.deploy.DeploymentData;

public interface RedhatDeploymentData extends DeploymentData {
	public ResourceSet getConfigurationFiles();

	public String getRunlevelsScript();

	public String getPermissionsScript();
}
