package org.argeo.slc.rpmfactory.core;

import java.util.List;

/** A consistent distributable set of RPM. */
public class RpmDistribution {
	private List<String> packages;

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}
}
