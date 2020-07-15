package org.argeo.slc.osgi;

import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.ResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.deploy.ModuleDescriptor;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.execution.RealizedFlow;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.core.io.Resource;

/** A deployed OSGi bundle. */
public class OsgiBundle extends DefaultNameVersion implements Module {
	private ResourceDistribution distribution;

	private Long internalBundleId;

	private String title;
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

	/**
	 * Initialize from a {@link RealizedFlow}.
	 * 
	 * @deprecated introduce an unnecessary dependency. TODO: create a separate
	 *             helper.
	 */
	public OsgiBundle(RealizedFlow realizedFlow) {
		super(realizedFlow.getModuleName(), realizedFlow.getModuleVersion());
	}

	/** Utility to avoid NPE. */
	private static String getVersionSafe(Bundle bundle) {
		Object versionObj = bundle.getHeaders().get(Constants.BUNDLE_VERSION);
		if (versionObj != null)
			return versionObj.toString();
		else
			return null;
	}

	/** Unique deployed system id. TODO: use internal bundle id when available? */
	public String getDeployedSystemId() {
		return getName() + ":" + getVersion();
	}

	/**
	 * OSGi bundle are self-contained and do not require additional deployment
	 * data.
	 * 
	 * @return always null
	 */
	public DeploymentData getDeploymentData() {
		return null;
	}

	/** The related distribution. */
	public Distribution getDistribution() {
		return distribution;
	}

	/**
	 * The related distribution, a jar file with OSGi metadata referenced by a
	 * {@link Resource}.
	 */
	public ResourceDistribution getResourceDistribution() {
		return distribution;
	}

	/** TODO: reference the {@link OsgiRuntime} as target data? */
	public TargetData getTargetData() {
		throw new UnsupportedOperationException();
	}

	public void setResourceDistribution(ResourceDistribution distribution) {
		this.distribution = distribution;
	}

	/**
	 * Bundle ID used by the OSGi runtime. To be used for optimization when
	 * looking in the bundle context. Can therefore be null.
	 */
	public Long getInternalBundleId() {
		return internalBundleId;
	}

	/** Only package access for the time being. e.g. from {@link BundlesManager} */
	void setInternalBundleId(Long internalBundleId) {
		this.internalBundleId = internalBundleId;
	}

	/** Value of the <code>Bundle-Name</code> directive. */
	public String getTitle() {
		return title;
	}

	public void setTitle(String label) {
		this.title = label;
	}

	/** Value of the <code>Bundle-Description</code> directive. */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ModuleDescriptor getModuleDescriptor() {
		ModuleDescriptor moduleDescriptor = new ModuleDescriptor();
		moduleDescriptor.setName(getName());
		moduleDescriptor.setVersion(getVersion());
		moduleDescriptor.setDescription(description);
		moduleDescriptor.setTitle(title);
		return moduleDescriptor;
	}
}
