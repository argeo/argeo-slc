package org.argeo.slc.deploy;

public interface DeployedSystemManager<T extends DeployedSystem> {
	public void setDeployedSystem(T deployedSystem);
}
