package org.argeo.slc.support.deploy;

import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.Deployment;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdServerDeployment implements Deployment {
	private HttpdServerTargetData targetData;

	public void execute() {
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
