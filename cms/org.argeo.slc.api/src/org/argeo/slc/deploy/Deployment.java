package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;

public interface Deployment extends Runnable{
	public DeployedSystem getDeployedSystem();

	public void setTargetData(TargetData targetData);

	public void setDeploymentData(DeploymentData deploymentData);

	public void setDistribution(Distribution distribution);
}
