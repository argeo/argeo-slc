package org.argeo.slc.core.deploy;

public interface DeployedSystemManager<T extends DeployedSystem> {
	public void setDeployedSystem(T deployedSystem);
}
