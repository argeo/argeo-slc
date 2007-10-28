package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystemId;

public class TimeTestResultId implements TestResultId{
	private DeployedSystemId deployedSystemId;
	private Long time;

	public void init() {
		time = System.currentTimeMillis();
	}

	public void setDeployedSystemId(DeployedSystemId deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	public DeployedSystemId getDeployedSystemId() {
		return deployedSystemId;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(toString());
	}

	@Override
	public String toString() {
		return time.toString();
	}

	

}
