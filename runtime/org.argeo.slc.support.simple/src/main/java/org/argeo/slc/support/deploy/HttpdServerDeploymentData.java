package org.argeo.slc.support.deploy;

import org.argeo.slc.deploy.DeploymentData;

public class HttpdServerDeploymentData implements DeploymentData {
	private String configFile;

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

}
