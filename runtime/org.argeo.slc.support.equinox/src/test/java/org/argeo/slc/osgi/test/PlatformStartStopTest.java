package org.argeo.slc.osgi.test;

import org.argeo.slc.equinox.unit.AbstractOsgiRuntimeTestCase;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class PlatformStartStopTest extends AbstractOsgiRuntimeTestCase {

	public void testStartStop() {
		BundleContext bundleContext = osgiBoot.getBundleContext();
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

	}
}
