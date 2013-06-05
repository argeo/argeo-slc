package org.argeo.slc.rpmfactory.core;

import java.util.List;

/** A consistent distributable set of RPM. */
public class RpmDistribution {
	private String id;
	private List<String> packages;
	private List<RpmPackageSet> excludedPackages;

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RpmPackageSet> getExcludedPackages() {
		return excludedPackages;
	}

	public void setExcludedPackages(List<RpmPackageSet> excludedPackages) {
		this.excludedPackages = excludedPackages;
	}

}
