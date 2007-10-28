package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystemId;

public class TimeTestResultId extends NumericTRId {
	private DeployedSystemId deployedSystemId;

	public void init() {
		if (getValue() == null) {
			setValue(System.currentTimeMillis());
		}
	}

	public void setDeployedSystemId(DeployedSystemId deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	public DeployedSystemId getDeployedSystemId() {
		return deployedSystemId;
	}
}
