/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.osgi;

import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.ResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.process.RealizedFlow;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.core.io.Resource;

/** A deployed OSGi bundle. */
public class OsgiBundle extends BasicNameVersion implements Module {
	private static final long serialVersionUID = -1970854723780452072L;

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
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/** Value of the <code>Bundle-Description</code> directive. */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
