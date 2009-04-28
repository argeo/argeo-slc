package org.argeo.slc.support.deploy;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class HttpdServerDeployment implements Deployment {
	private HttpdServerTargetData targetData;

	public void run() {
		// TODO Auto-generated method stub

	}

	public DeployedSystem getDeployedSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		// TODO Auto-generated method stub

	}

	public void setDistribution(Distribution distribution) {
	}

	public void setTargetData(TargetData targetData) {
		this.targetData = (HttpdServerTargetData) targetData;
	}

}
