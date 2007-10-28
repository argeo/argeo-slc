package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystemId;

public class NumericTRId implements TestResultId {
	/** For ORM */
	private Long tid;

	private Long value;
	private DeployedSystemId deployedSystemId;
	
	public void init() {
		if (getValue() == null) {
			setValue(System.currentTimeMillis());
		}
	}


	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public DeployedSystemId getDeployedSystemId() {
		return deployedSystemId;
	}

	public void setDeployedSystemId(DeployedSystemId deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	@Override
	public boolean equals(Object obj) {
		return value.toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}
	
	

}
