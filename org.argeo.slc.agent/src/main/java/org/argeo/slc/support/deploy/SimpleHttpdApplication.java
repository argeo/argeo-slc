package org.argeo.slc.support.deploy;

import java.io.File;
import java.net.URL;

import org.argeo.slc.core.build.Distribution;

public class SimpleHttpdApplication implements WebApplication {
	private HttpdApplicationTargetData targetData;
	private Distribution distribution;

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public URL getBaseUrl() {
		return targetData.getTargetBaseUrl();
	}

	public File getRootLocation() {
		return targetData.getTargetRootLocation();
	}

	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpdApplicationTargetData getTargetData() {
		return targetData;
	}

	public void setTargetData(HttpdApplicationTargetData targetData) {
		this.targetData = targetData;
	}

	public Distribution getDistribution() {
		return distribution;
	}

}
