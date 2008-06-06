package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.Distribution;

public interface Deployment {
	public DeployedSystem getDeployedSystem();

	public void setTargetData(TargetData targetData);

	public void setDeploymentData(DeploymentData deploymentData);

	public void setDistribution(Distribution distribution);

	public void execute();

}
