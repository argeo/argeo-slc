package org.argeo.slc.osgi.test;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.equinox.unit.AbstractOsgiRuntimeTestCase;
import org.argeo.slc.osgiboot.OsgiBoot;

public class HelloTest extends AbstractOsgiRuntimeTestCase {
	public void testHello() throws Exception {
		Thread.sleep(2000);
	}

	protected void installBundles() throws Exception {
//		System.out.println("java.class.path="
//				+ System.getProperty("java.class.path"));

		osgiBoot.installUrls(osgiBoot.getLocationsUrls(
				OsgiBoot.DEFAULT_BASE_URL, System
						.getProperty("java.class.path")));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"src/test/bundles;in=*"));

		// Map<String, String> sysProps = new TreeMap(System.getProperties());
		// for (String key : sysProps.keySet()) {
		// System.out.println(key + "=" + sysProps.get(key));
		// }
	}

	protected List<String> getBundlesToStart() {
		List<String> bundlesToStart = new ArrayList<String>();
		// bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.slc.support.osgi.test.hello");
		return bundlesToStart;
	}

}
