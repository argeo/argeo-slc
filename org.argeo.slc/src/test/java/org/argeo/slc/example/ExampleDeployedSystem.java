package org.argeo.slc.example;

import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.DeployedSystemId;
import org.argeo.slc.example.appli.ExampleAppli;

public class ExampleDeployedSystem implements DeployedSystem {
	private DeployedSystemId deployedSystemId;

	public DeployedSystemId getDeployedSystemId() {
		return deployedSystemId;
	}

	public void setDeployedSystemId(DeployedSystemId deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	public ExampleAppli getExampleAppliInstance(){
		return new ExampleAppli();
	}
}
