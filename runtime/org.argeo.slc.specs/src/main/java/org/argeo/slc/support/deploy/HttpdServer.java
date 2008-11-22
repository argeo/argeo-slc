package org.argeo.slc.support.deploy;

import java.io.IOException;
import java.net.URL;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdServer implements WebServer {
	private HttpdServerTargetData targetData;
	private HttpdServerDeploymentData deploymentData;

	public URL getBaseUrl() {
		try {
			return new URL("http://localhost:" + targetData.getPort());
		} catch (IOException e) {
			throw new SlcException("Cannot get url for Httpd server "
					+ getDeployedSystemId(), e);
		}
	}

	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Distribution getDistribution() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeploymentData getDeploymentData() {
		return deploymentData;
	}

	public TargetData getTargetData() {
		return targetData;
	}

	public void setTargetData(TargetData targetData) {
		this.targetData = (HttpdServerTargetData)targetData;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = (HttpdServerDeploymentData)deploymentData;
	}

}
