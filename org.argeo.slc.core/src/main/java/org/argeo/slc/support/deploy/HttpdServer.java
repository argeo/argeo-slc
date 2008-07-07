package org.argeo.slc.support.deploy;

import java.io.IOException;
import java.net.URL;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.build.Distribution;

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

	public HttpdServerDeploymentData getDeploymentData() {
		return deploymentData;
	}

	public HttpdServerTargetData getTargetData() {
		return targetData;
	}

	public void setTargetData(HttpdServerTargetData targetData) {
		this.targetData = targetData;
	}

	public void setDeploymentData(HttpdServerDeploymentData deploymentData) {
		this.deploymentData = deploymentData;
	}

}
