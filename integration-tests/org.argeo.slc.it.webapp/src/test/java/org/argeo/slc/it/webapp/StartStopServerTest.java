package org.argeo.slc.it.webapp;

import java.util.Properties;

import org.argeo.slc.osgi.test.AbstractOsgiRuntimeTest;
import org.argeo.slc.osgiboot.OsgiBoot;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.test.platform.EquinoxPlatform;
import org.springframework.osgi.test.platform.OsgiPlatform;
import org.springframework.osgi.util.OsgiStringUtils;

public class StartStopServerTest extends AbstractOsgiRuntimeTest {
	public void testStartStop() throws Exception {
		BundleContext bundleContext = osgiPlatform.getBundleContext();
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out
					.println(OsgiStringUtils.nullSafeSymbolicName(bundles[i]));
		}

		Thread.sleep(5 * 1000);

	}

	protected void postStart() throws Exception {
		String classpath = System.getProperty("java.class.path");
		System.out.println("Classpath=" + classpath);
		osgiBoot.installUrls(osgiBoot.getLocationsUrls(
				OsgiBoot.DEFAULT_BASE_URL, classpath));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"../../demo/site;in=*;ex=target"));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls(OsgiBoot.DEFAULT_BASE_URL,
				"target/dependency;in=*.jar"));

		String bundlesToStart = "org.argeo.dep.osgi.catalina.start,org.springframework.osgi.extender,org.springframework.osgi.web.extender,org.springframework.osgi.samples.simplewebapp,org.argeo.slc.server.activemq,org.argeo.slc.server.hsqldb,org.argeo.slc.server.hibernate,org.argeo.slc.server.services,org.argeo.slc.server.jms,org.argeo.slc.webapp,org.argeo.slc.ria";
		osgiBoot.startBundles(bundlesToStart);
	}

	@Override
	protected OsgiPlatform createOsgiPlatform() {

		return new EquinoxPlatform() {

			@Override
			public Properties getConfigurationProperties() {
				Properties props = super.getConfigurationProperties();
				props.put("osgi.console", "");
				return props;
			}

		};
	}

}
