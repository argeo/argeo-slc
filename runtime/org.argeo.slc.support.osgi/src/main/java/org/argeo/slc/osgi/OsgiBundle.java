package org.argeo.slc.osgi;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.process.RealizedFlow;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class OsgiBundle implements Module {
	private String name;
	private String version;
	private Distribution distribution;

	private Long internalBundleId;

	public OsgiBundle() {

	}

	public OsgiBundle(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public OsgiBundle(Bundle bundle) {
		name = bundle.getSymbolicName();
		version = bundle.getHeaders().get(Constants.BUNDLE_VERSION).toString();
		internalBundleId = bundle.getBundleId();
	}

	public OsgiBundle(RealizedFlow realizedFlow) {
		name = realizedFlow.getModuleName();
		version = realizedFlow.getModuleVersion();
	}

	public String getDeployedSystemId() {
		return name + ":" + version;
	}

	public DeploymentData getDeploymentData() {
		throw new UnsupportedOperationException();
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public TargetData getTargetData() {
		throw new UnsupportedOperationException();
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

	/**
	 * To be used for optimization when looking in the bundle context. Can
	 * therefore be null.
	 */
	public Long getInternalBundleId() {
		return internalBundleId;
	}

	/** Only package access for the time being. e.g. from {@link BundlesManager} */
	void setInternalBundleId(Long internalBundleId) {
		this.internalBundleId = internalBundleId;
	}

}
