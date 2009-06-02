package org.argeo.slc.osgi.test;

import org.argeo.slc.osgiboot.OsgiBoot;

public class HelloTest extends AbstractOsgiRuntimeTest {
	public void testHello() throws Exception {
		Thread.sleep(5000);
	}

	protected void postStart() throws Exception {
		osgiBoot.installUrls(osgiBoot.getLocationsUrls(
				OsgiBoot.DEFAULT_BASE_URL, System
						.getProperty("java.class.path")));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"src/test/bundles;in=*"));

		listInstalledBundles();
		String bundlesToStart = "org.springframework.osgi.extender,org.argeo.slc.support.osgi.test.hello";
		osgiBoot.startBundles(bundlesToStart);

	}

}
