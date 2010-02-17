package org.argeo.slc.osgi;

import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.core.build.ResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.process.RealizedFlow;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class OsgiBundle extends BasicNameVersion implements Module {
	private ResourceDistribution distribution;

	private Long internalBundleId;

	private String label;
	private String description;

	public OsgiBundle() {

	}

	public OsgiBundle(String name, String version) {
		super(name, version);
	}

	public OsgiBundle(NameVersion nameVersion) {
		super(nameVersion);
	}

	public OsgiBundle(Bundle bundle) {
		super(bundle.getSymbolicName(), getVersionSafe(bundle));
		internalBundleId = bundle.getBundleId();
	}

	private static String getVersionSafe(Bundle bundle) {
		Object versionObj = bundle.getHeaders().get(Constants.BUNDLE_VERSION);
		if (versionObj != null)
			return versionObj.toString();
		else
			return null;
	}

	public OsgiBundle(RealizedFlow realizedFlow) {
		super(realizedFlow.getModuleName(), realizedFlow.getModuleVersion());
	}

	public String getDeployedSystemId() {
		return getName() + ":" + getVersion();
	}

	public DeploymentData getDeploymentData() {
		throw new UnsupportedOperationException();
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public ResourceDistribution getResourceDistribution() {
		return distribution;
	}

	public TargetData getTargetData() {
		throw new UnsupportedOperationException();
	}

	public void setResourceDistribution(ResourceDistribution distribution) {
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
