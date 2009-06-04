package org.argeo.slc.demo;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.osgi.test.AbstractOsgiRuntimeTestCase;
import org.argeo.slc.osgiboot.OsgiBoot;

public class StartStopDemoTest extends AbstractOsgiRuntimeTestCase {
	public void testStartStop() throws Exception {
		assertBundleApplicationContext("org.argeo.slc.agent");
		assertStartedBundlesApplicationContext("org.argeo.slc.demo");
	}

	protected void installBundles() throws Exception {
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"target/dependency;in=*.jar"));
		osgiBoot.installUrls(osgiBoot.getLocationsUrls(
				OsgiBoot.DEFAULT_BASE_URL, System
						.getProperty("java.class.path")));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"site;in=*"));
	}

	protected List<String> getBundlesToStart() {
		List<String> bundlesToStart = new ArrayList<String>();
		bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.slc.agent");
		bundlesToStart.add("org.argeo.slc.demo.basic");
		return bundlesToStart;
	}

}
