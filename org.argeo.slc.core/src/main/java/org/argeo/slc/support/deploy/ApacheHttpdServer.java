package org.argeo.slc.support.deploy;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;
import org.springframework.core.io.Resource;

public class ApacheHttpdServer implements WebServer {
	private Resource baseUrlRes;
	private File baseLocation;

	public URL getBaseUrl() {
		try {
			return baseUrlRes.getURL();
		} catch (IOException e) {
			throw new SlcException("Cannot get url from "+baseUrlRes,e);
		}
	}

	public void setBaseUrlRes(Resource baseUrlRes){
		this.baseUrlRes = baseUrlRes;
	}
	
	
	
	public File getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(File baseLocation) {
		this.baseLocation = baseLocation;
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
		// TODO Auto-generated method stub
		return null;
	}

	public TargetData getTargetData() {
		// TODO Auto-generated method stub
		return null;
	}

}
