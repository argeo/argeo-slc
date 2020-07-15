package org.argeo.eclipse.spring;

import static java.text.MessageFormat.format;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

/** Tracks Spring application context published as services. */
class ApplicationContextTracker {
	private final static Log log = LogFactory
			.getLog(ApplicationContextTracker.class);

	private static final String FILTER = "(&(objectClass=org.springframework.context.ApplicationContext)" //$NON-NLS-1$
			+ "(org.springframework.context.service.name={0}))"; //$NON-NLS-1$

	public final static String APPLICATION_CONTEXT_TRACKER_TIMEOUT = "org.argeo.eclipse.spring.applicationContextTrackerTimeout";

	private static Long defaultTimeout = Long.parseLong(System.getProperty(
			APPLICATION_CONTEXT_TRACKER_TIMEOUT, "30000"));

	@SuppressWarnings("rawtypes")
	private ServiceTracker applicationContextServiceTracker;

	/**
	 * @param contributorBundle
	 *            OSGi bundle for which the Spring application context is to be
	 *            tracked. Must not be null!
	 * @param factoryBundleContext
	 *            BundleContext object which can be used to track services
	 * @throws IllegalArgumentException
	 *             if the given bundle is null.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ApplicationContextTracker(final Bundle contributorBundle,
			final BundleContext factoryBundleContext) {
		final String filter = format(FILTER,
				contributorBundle.getSymbolicName());
		try {
			applicationContextServiceTracker = new ServiceTracker(
					factoryBundleContext, FrameworkUtil.createFilter(filter),
					null);
			// applicationContextServiceTracker.open();
		} catch (final InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void open() {
		if (applicationContextServiceTracker != null) {
			applicationContextServiceTracker.open();
		}
	}

	public void close() {
		if (applicationContextServiceTracker != null) {
			applicationContextServiceTracker.close();
		}
	}

	public ApplicationContext getApplicationContext() {
		ApplicationContext applicationContext = null;
		if (applicationContextServiceTracker != null) {
			try {
				applicationContext = (ApplicationContext) applicationContextServiceTracker
						.waitForService(defaultTimeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return applicationContext;
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	static ApplicationContext getApplicationContext(String bundleSymbolicName) {
		Bundle contributorBundle = Platform.getBundle(bundleSymbolicName);
		return getApplicationContext(contributorBundle);
	}

	static ApplicationContext getApplicationContext(
			final Bundle contributorBundle) {
		if (log.isTraceEnabled())
			log.trace("Get application context for bundle " + contributorBundle);

		// Start if not yet started (also if in STARTING state, may be lazy)
		if (contributorBundle.getState() != Bundle.ACTIVE) {
			if (log.isTraceEnabled())
				log.trace("Starting bundle: "
						+ contributorBundle.getSymbolicName());
			// Thread startBundle = new Thread("Start bundle "
			// + contributorBundle.getSymbolicName()) {
			// public void run() {
			try {
				contributorBundle.start();
			} catch (BundleException e) {
				log.error("Cannot start bundle " + contributorBundle, e);
			}
			// }
			// };
			// startBundle.start();
			// try {
			// startBundle.join(10 * 1000l);
			// } catch (InterruptedException e) {
			// // silent
			// }
		}

		final ApplicationContextTracker applicationContextTracker = new ApplicationContextTracker(
				contributorBundle, contributorBundle.getBundleContext());
		ApplicationContext applicationContext = null;
		try {
			applicationContextTracker.open();
			applicationContext = applicationContextTracker
					.getApplicationContext();
		} finally {
			applicationContextTracker.close();
		}
		return applicationContext;
	}
}
