package org.argeo.slc.core.deploy;

public interface ManageableDeployedSystem extends DeployedSystem {
	public void start();

	public void stop();
}
