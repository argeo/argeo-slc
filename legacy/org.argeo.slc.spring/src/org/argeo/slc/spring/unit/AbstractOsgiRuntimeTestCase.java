package org.argeo.slc.spring.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.init.osgi.OsgiBoot;
import org.argeo.slc.SlcException;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public abstract class AbstractOsgiRuntimeTestCase extends TestCase {
	private final static Log log = LogFactory
			.getLog(AbstractOsgiRuntimeTestCase.class);

	protected OsgiBoot osgiBoot = null;

	protected void installBundles() throws Exception {

	}

	public void setUp() throws Exception {
		// To avoid xerces from the classpath being detected as the provider
		System
				.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

		BundleContext bundleContext = startRuntime();
		osgiBoot = new OsgiBoot(bundleContext);
		log.info("OSGi runtime started.");

		installBundles();

		List<String> bundlesToStart = getBundlesToStart();
		osgiBoot.startBundles(bundlesToStart);
		waitAllBundlesOk(bundlesToStart);
		if (log.isTraceEnabled())
			listInstalledBundles();
	}

	public void tearDown() throws Exception {
		osgiBoot = null;
		stopRuntime();
		log.info("OSGi runtime stopped.");
	}

	protected BundleContext startRuntime() throws Exception {
		String[] args = { "-console", "-clean" };
		BundleContext bundleContext = EclipseStarter.startup(args, null);
		return bundleContext;
	}

	protected void stopRuntime() throws Exception {
		EclipseStarter.shutdown();
	}

	protected List<String> getBundlesToStart() {
		return new ArrayList<String>();
	}

	protected void listInstalledBundles() {
		BundleContext bundleContext = osgiBoot.getBundleContext();
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out.println(OsgiStringUtils.nullSafeSymbolicName(bundles[i])
					+ " [" + OsgiStringUtils.bundleStateAsString(bundles[i])
					+ "] " + bundles[i].getLocation());
		}

	}

	protected Map<Bundle, ApplicationContext> getOsgiApplicationContexts()
			throws Exception {
		Map<Bundle, ApplicationContext> map = new HashMap<Bundle, ApplicationContext>();
		BundleContext bundleContext = osgiBoot.getBundleContext();
		ServiceReference[] srs = bundleContext.getServiceReferences(
				ApplicationContext.class.getName(), null);
		for (ServiceReference sr : srs) {
			ApplicationContext context = (ApplicationContext) bundleContext
					.getService(sr);
			map.put(sr.getBundle(), context);
		}
		return map;
	}

	/** Wait for all bundles to be either RESOLVED or ACTIVE. */
	protected void waitAllBundlesOk(List<String> bundlesToStart) {
		BundleContext bundleContext = osgiBoot.getBundleContext();
		long begin = System.currentTimeMillis();
		long duration = 0;
		boolean allBundlesOk = true;
		StringBuffer badBundles = null;
		while (duration < getResolvedTimeout()) {
			badBundles = new StringBuffer();
			for (Bundle bundle : bundleContext.getBundles()) {
				if (bundle.getSymbolicName() != null
						&& bundle.getSymbolicName().startsWith(
								"org.eclipse.jdt")) {
					// don't check Eclipse SDK bundles
					continue;
				}

				if (bundle.getState() == Bundle.INSTALLED) {
					allBundlesOk = false;
					badBundles
							.append(OsgiStringUtils
									.nullSafeSymbolicName(bundle)
									+ " ["
									+ OsgiStringUtils
											.bundleStateAsString(bundle) + "]");
				}

				if (bundlesToStart.contains(bundle.getSymbolicName())
						&& bundle.getState() != Bundle.ACTIVE) {
					allBundlesOk = false;
					badBundles.append(OsgiStringUtils
							.nullSafeSymbolicName(bundle)
							+ " ["
							+ OsgiStringUtils.bundleStateAsString(bundle)
							+ "]\n");
				}
			}

			if (allBundlesOk)
				break;// while

			sleep(1000);

			duration = System.currentTimeMillis() - begin;
		}

		if (!allBundlesOk) {
			listInstalledBundles();
			throw new SlcException(
					"Some bundles are not at the proper status:\n" + badBundles);
		}
	}

	/**
	 * Make sure that the application context of the started bundles starting
	 * with this prefix are properly initialized
	 */
	protected void assertStartedBundlesApplicationContext(
			String bundleSymbolicNamesPrefix) {
		List<String> bundlesToStart = getBundlesToStart();
		for (String bundleSName : bundlesToStart) {
			if (bundleSName.startsWith(bundleSymbolicNamesPrefix))
				assertBundleApplicationContext(bundleSName);
		}
	}

	/**
	 * Make sure that the application context of this bundle is properly
	 * initialized
	 */
	protected void assertBundleApplicationContext(String bundleSymbolicName) {
		String filter = "(Bundle-SymbolicName=" + bundleSymbolicName + ")";
		// Wait for application context to be ready
		try {
			ServiceReference[] srs = getServiceRefSynchronous(
					ApplicationContext.class.getName(), filter);
			if (srs == null)
				throw new SlcException("No application context for "
						+ bundleSymbolicName);
		} catch (InvalidSyntaxException e) {
			throw new SlcException(
					"Unexpected exception when looking for application context for bundle "
							+ bundleSymbolicName, e);
		}
		log.info("Application context of bundle " + bundleSymbolicName
				+ " is initalized.");
	}

	protected ServiceReference[] getServiceRefSynchronous(String clss,
			String filter) throws InvalidSyntaxException {
		// FIXME: factorize
		if (log.isTraceEnabled())
			log.debug("Filter: '" + filter + "'");
		ServiceReference[] sfs = null;
		boolean waiting = true;
		long begin = System.currentTimeMillis();
		do {
			sfs = getBundleContext().getServiceReferences(clss, filter);

			if (sfs != null)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > getDefaultTimeout())
				throw new SlcException("Search of services " + clss
						+ " with filter " + filter + " timed out.");
		} while (waiting);

		return sfs;
	}

	protected BundleContext getBundleContext() {
		return osgiBoot.getBundleContext();
	}

	/** Default is 30s */
	protected long getResolvedTimeout() {
		return 30 * 1000l;
	}

	/** Default is 10s */
	protected long getDefaultTimeout() {
		return 10 * 1000l;
	}

	final protected void sleep(long duration) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// silent
		}
	}
}
