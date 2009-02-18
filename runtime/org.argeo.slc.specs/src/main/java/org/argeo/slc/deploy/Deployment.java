package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.process.Executable;

public interface Deployment extends Executable{
	public DeployedSystem getDeployedSystem();

	public void setTargetData(TargetData targetData);

	public void setDeploymentData(DeploymentData deploymentData);

	public void setDistribution(Distribution distribution);

	public void execute();

}
