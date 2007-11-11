package org.argeo.slc.core.deploy;

public interface WritableDeployment extends ExecutableDeployment{
	public void setDeployedSystem(DeployedSystem deployedSystem);

	public void setTargetData(TargetData targetData);

	public void setDeploymentData(DeploymentData deploymentData);
}
