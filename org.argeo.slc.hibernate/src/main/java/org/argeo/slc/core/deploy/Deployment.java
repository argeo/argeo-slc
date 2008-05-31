package org.argeo.slc.core.deploy;

public interface Deployment {
	public DeployedSystem getDeployedSystem();

	public TargetData getTargetData();

	public DeploymentData getDeploymentData();
}
