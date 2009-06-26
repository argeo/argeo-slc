package org.argeo.slc.osgiboot;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		try {
			OsgiBoot.info("SLC OSGi bootstrap starting...");
			OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
			osgiBoot.installUrls(osgiBoot.getBundlesUrls());
			osgiBoot.installUrls(osgiBoot.getLocationsUrls());
			osgiBoot.startBundles();
			OsgiBoot.info("SLC OSGi bootstrap completed");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void stop(BundleContext context) throws Exception {
	}
}
