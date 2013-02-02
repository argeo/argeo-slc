/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.StreamReadable;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.VersionedResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.deploy.TargetData;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;

public class OsgiRuntime implements BundleContextAware, ResourceLoaderAware,
		DynamicRuntime<OsgiBundle> {
	private String uuid = UUID.randomUUID().toString();
	private BundleContext bundleContext;
	private ResourceLoader resourceLoader;

	public List<OsgiBundle> listModules() {
		List<OsgiBundle> modules = new ArrayList<OsgiBundle>();
		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			OsgiBundle osgiBundle = new OsgiBundle(bundle);
			modules.add(osgiBundle);
			String location = bundle.getLocation();
			if (location != null) {
				Resource resource = resourceLoader.getResource(location);
				osgiBundle
						.setResourceDistribution(new VersionedResourceDistribution(
								osgiBundle.getName(), osgiBundle.getVersion(),
								resource));
			}
		}
		return modules;
	}

	public OsgiBundle installModule(Distribution distribution) {
		if (!(distribution instanceof StreamReadable))
			throw new UnsupportedException("distribution", distribution);

		StreamReadable sr = (StreamReadable) distribution;
		Bundle bundle;
		try {
			bundle = bundleContext.installBundle(sr.toString(), sr
					.getInputStream());
		} catch (BundleException e) {
			throw new SlcException(
					"Cannot install OSGi bundle " + distribution, e);
		}
		return new OsgiBundle(bundle);
	}

	public void updateModule(NameVersion nameVersion) {
		Bundle bundle = findBundle(nameVersion);
		try {
			bundle.update();
		} catch (BundleException e) {
			throw new SlcException("Cannot update " + bundle, e);
		}
	}

	public void uninstallModule(NameVersion nameVersion) {
		Bundle bundle = findBundle(nameVersion);
		try {
			bundle.uninstall();
		} catch (BundleException e) {
			throw new SlcException("Cannot uninstall " + bundle, e);
		}
	}

	public void startModule(NameVersion nameVersion) {
		Bundle bundle = findBundle(nameVersion);
		try {
			bundle.start();
			// TODO: use bundle manager
		} catch (BundleException e) {
			throw new SlcException("Cannot uninstall " + bundle, e);
		}
	}

	protected Bundle findBundle(NameVersion nameVersion) {
		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			OsgiBundle osgiBundle = new OsgiBundle(bundle);
			if (osgiBundle.equals(nameVersion)) {
				return bundle;
			}
		}
		throw new SlcException("Could not find bundle " + nameVersion);
	}

	public void shutdown() {
		// FIXME use framework
		throw new UnsupportedException();
	}

	public String getDeployedSystemId() {
		return uuid;
	}

	public DeploymentData getDeploymentData() {
		throw new UnsupportedException();
	}

	public Distribution getDistribution() {
		throw new UnsupportedException();
	}

	public TargetData getTargetData() {
		throw new UnsupportedException();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
