package org.argeo.slc.osgiboot;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OsgiBootRuntimeTest extends TestCase {
	protected OsgiBoot osgiBoot = null;

	public void testInstallAndStart() throws Exception {
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				OsgiBootNoRuntimeTest.BUNDLES));
		Map map = new TreeMap(osgiBoot.getBundles());
		for (Iterator keys = map.keySet().iterator(); keys.hasNext();) {
			Object key = keys.next();
			Bundle bundle = (Bundle) map.get(key);
			System.out.println(key + " : " + bundle.getLocation());
		}
		assertEquals(4, map.size());
		Iterator keys = map.keySet().iterator();
		assertEquals("org.argeo.slc.osgiboot.test.bundle1", keys.next());
		assertEquals("org.argeo.slc.osgiboot.test.bundle2", keys.next());
		assertEquals("org.argeo.slc.osgiboot.test.bundle3", keys.next());
		assertEquals("org.eclipse.osgi", keys.next());

		osgiBoot.startBundles("org.argeo.slc.osgiboot.test.bundle2");
		long begin = System.currentTimeMillis();
		while (System.currentTimeMillis() - begin < 10000) {
			Map mapBundles = osgiBoot.getBundles();
			Bundle bundle = (Bundle) mapBundles
					.get("org.argeo.slc.osgiboot.test.bundle2");
			if (bundle.getState() == Bundle.ACTIVE) {
				System.out.println("Bundle " + bundle + " started.");
				return;
			}
		}
		fail("Bundle not started after timeout limit.");
	}

	protected BundleContext startRuntime() throws Exception {
		String[] args = { "-console" };
		BundleContext bundleContext = EclipseStarter.startup(args, null);
		return bundleContext;
	}

	protected void stopRuntime() throws Exception {
		EclipseStarter.shutdown();
	}

	public void setUp() throws Exception{
		BundleContext bundleContext = startRuntime();
		osgiBoot = new OsgiBoot(bundleContext);
	}
	
	public void tearDown() throws Exception{
		osgiBoot = null;
		stopRuntime();
	}

}
