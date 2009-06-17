package org.argeo.slc.osgi;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.deploy.ResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.deploy.TargetData;
import org.osgi.framework.Bundle;

public class OsgiBundle implements Module<ResourceDistribution> {
	private String name;
	private String version;
	private Distribution distribution;

	public OsgiBundle() {

	}

	public OsgiBundle(Bundle bundle) {
		name = bundle.getSymbolicName();
		version = bundle.getHeaders().get("Bundle-Version").toString();
	}

	public String getDeployedSystemId() {
		return name + ":" + version;
	}

	public DeploymentData getDeploymentData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public TargetData getTargetData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

}
