package org.argeo.slc.core.deploy;

public class SimpleDeployedSytemManager implements
		DeployedSystemManager<ManageableDeployedSystem> {

	private ManageableDeployedSystem deployedSystem;

	public void setDeployedSystem(ManageableDeployedSystem deployedSystem) {
		this.deployedSystem = deployedSystem;
	}

}
