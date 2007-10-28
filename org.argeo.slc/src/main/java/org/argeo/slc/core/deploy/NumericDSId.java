package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.DistributionId;

public class NumericDSId implements DeployedSystemId {

	private Long value;
	private DistributionId distributionId;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public DistributionId getDistributionId() {
		return distributionId;
	}

	public void setDistributionId(DistributionId distributionId) {
		this.distributionId = distributionId;
	}

	@Override
	public boolean equals(Object obj) {
		return value.toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return value.toString();
	}

	
}
