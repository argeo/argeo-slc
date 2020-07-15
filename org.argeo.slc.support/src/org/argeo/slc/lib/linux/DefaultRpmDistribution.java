package org.argeo.slc.lib.linux;

import java.util.List;

public class DefaultRpmDistribution implements RpmDistribution {
	private List<String> additionalPackages;

	public String getDistributionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getAdditionalPackages() {
		return additionalPackages;
	}

	public void setAdditionalPackages(List<String> additionalPackages) {
		this.additionalPackages = additionalPackages;
	}

}
