package org.argeo.slc.lib.linux;

import java.util.List;

import org.argeo.slc.build.Distribution;

public class RpmDistribution implements Distribution {
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
