package org.argeo.slc.lib.linux;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class SimpleLinuxHost implements DeployedSystem {
	private DeploymentData deploymentData;
	private Distribution distribution;
	private TargetData targetData;

	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeploymentData getDeploymentData() {
		return deploymentData;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = deploymentData;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public TargetData getTargetData() {
		return targetData;
	}

	public void setTargetData(TargetData targetData) {
		this.targetData = targetData;
	}

}
