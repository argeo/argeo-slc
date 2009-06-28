package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.VersionedResourceDistribution;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.ModularDeployedSystem;
import org.argeo.slc.deploy.TargetData;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;

public class OsgiRuntime implements ModularDeployedSystem<OsgiBundle>,
		BundleContextAware, ResourceLoaderAware {
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
						.setDistribution(new VersionedResourceDistribution(
								osgiBundle.getName(), osgiBundle.getVersion(),
								resource));
			}
		}
		return modules;
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
