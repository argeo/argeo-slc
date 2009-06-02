package org.argeo.slc.osgi.test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class PlatformStartStopTest extends AbstractOsgiRuntimeTest {

	public void testStartStop() {
		BundleContext bundleContext = osgiPlatform.getBundleContext();
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

	}
}
