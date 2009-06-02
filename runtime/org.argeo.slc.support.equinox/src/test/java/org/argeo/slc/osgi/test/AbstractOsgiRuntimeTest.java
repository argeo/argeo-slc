package org.argeo.slc.osgi.test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.osgiboot.OsgiBoot;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.test.platform.EquinoxPlatform;
import org.springframework.osgi.test.platform.OsgiPlatform;
import org.springframework.osgi.util.OsgiStringUtils;

public abstract class AbstractOsgiRuntimeTest extends TestCase {
	private final static Log log = LogFactory
			.getLog(AbstractOsgiRuntimeTest.class);

	protected OsgiBoot osgiBoot = null;
	protected OsgiPlatform osgiPlatform = null;

	protected OsgiPlatform createOsgiPlatform() {
		return new EquinoxPlatform();
	}

	protected void postStart() throws Exception {

	}

	public void setUp() throws Exception {
		// To avoid xerces from the classpath being detected as the provider
		System
				.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

		osgiPlatform = createOsgiPlatform();
		osgiPlatform.start();
		osgiBoot = new OsgiBoot(osgiPlatform.getBundleContext());
		log.info("OSGi platform " + osgiPlatform + " started.");
		postStart();
	}

	public void tearDown() throws Exception {
		osgiBoot = null;
		osgiPlatform.stop();
		osgiPlatform = null;
		log.info("OSGi platform " + osgiPlatform + " stopped.");
	}

	protected void listInstalledBundles() {
		BundleContext bundleContext = osgiPlatform.getBundleContext();
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out
					.println(OsgiStringUtils.nullSafeSymbolicName(bundles[i]));
		}

	}
}
