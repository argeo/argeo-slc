package org.argeo.slc.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.OsgiFilterUtils;
import org.springframework.util.Assert;

/** Wraps low-level access to a {@link BundleContext} */
public class BundlesManager implements BundleContextAware, FrameworkListener,
		InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(BundlesManager.class);

	private BundleContext bundleContext;

	private Long defaultTimeout = 10000l;
	private final Object refreshedPackageSem = new Object();

	/**
	 * Stop the module, update it, refresh it and restart it. All synchronously.
	 */
	public void upgradeSynchronous(OsgiBundle osgiBundle) {
		try {
			Bundle bundle = findRelatedBundle(osgiBundle);
			stopSynchronous(bundle);
			updateSynchronous(bundle);
			// Refresh in case there are fragments
			refreshSynchronous(bundle);
			startSynchronous(bundle);

			String filter = "(Bundle-SymbolicName=" + bundle.getSymbolicName()
					+ ")";
			// Wait for application context to be ready
			// TODO: use service tracker
			getServiceRefSynchronous(ApplicationContext.class.getName(), filter);

			if (log.isDebugEnabled())
				log.debug("Bundle " + bundle.getSymbolicName()
						+ " ready to be used at latest version.");
		} catch (Exception e) {
			throw new SlcException("Cannot update bundle " + osgiBundle, e);
		}
	}

	/** Updates bundle synchronously. */
	protected void updateSynchronous(Bundle bundle) throws BundleException {
		// int originalState = bundle.getState();
		bundle.update();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			int state = bundle.getState();
			if (state == Bundle.INSTALLED || state == Bundle.ACTIVE
					|| state == Bundle.RESOLVED)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Update of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " updated.");
	}

	/** Starts bundle synchronously. Does nothing if already started. */
	protected void startSynchronous(Bundle bundle) throws BundleException {
		int originalState = bundle.getState();
		if (originalState == Bundle.ACTIVE)
			return;

		bundle.start();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			if (bundle.getState() == Bundle.ACTIVE)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Start of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " started.");
	}

	/** Stops bundle synchronously. Does nothing if already started. */
	protected void stopSynchronous(Bundle bundle) throws BundleException {
		int originalState = bundle.getState();
		if (originalState != Bundle.ACTIVE)
			return;

		bundle.stop();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			if (bundle.getState() != Bundle.ACTIVE
					&& bundle.getState() != Bundle.STOPPING)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Stop of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " stopped.");
	}

	/** Refresh bundle synchronously. Does nothing if already started. */
	protected void refreshSynchronous(Bundle bundle) throws BundleException {
		ServiceReference packageAdminRef = bundleContext
				.getServiceReference(PackageAdmin.class.getName());
		PackageAdmin packageAdmin = (PackageAdmin) bundleContext
				.getService(packageAdminRef);
		Bundle[] bundles = { bundle };
		packageAdmin.refreshPackages(bundles);

		synchronized (refreshedPackageSem) {
			try {
				refreshedPackageSem.wait(defaultTimeout);
			} catch (InterruptedException e) {
				// silent
			}
		}

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " refreshed.");
	}

	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
			synchronized (refreshedPackageSem) {
				refreshedPackageSem.notifyAll();
			}
		}
	}

	public ServiceReference[] getServiceRefSynchronous(String clss,
			String filter) throws InvalidSyntaxException {
		if (log.isTraceEnabled())
			log.debug("Filter: '" + filter + "'");
		ServiceReference[] sfs = null;
		boolean waiting = true;
		long begin = System.currentTimeMillis();
		do {
			sfs = bundleContext.getServiceReferences(clss, filter);

			if (sfs != null)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new SlcException("Search of services " + clss
						+ " with filter " + filter + " timed out.");
		} while (waiting);

		return sfs;
	}

	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// silent
		}
	}

	/** Creates and open a new service tracker. */
	public ServiceTracker newTracker(Class<?> clss) {
		ServiceTracker st = new ServiceTracker(bundleContext, clss.getName(),
				null);
		st.open();
		return st;
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T> T getSingleService(Class<T> clss, String filter) {
		Assert.isTrue(OsgiFilterUtils.isValidFilter(filter), "valid filter");
		ServiceReference[] sfs;
		try {
			sfs = bundleContext.getServiceReferences(clss.getName(), filter);
		} catch (InvalidSyntaxException e) {
			throw new SlcException("Cannot retrieve service reference for "
					+ filter, e);
		}

		if (sfs == null || sfs.length == 0)
			return null;
		else if (sfs.length > 1)
			throw new SlcException("More than one execution flow found for "
					+ filter);
		return (T) bundleContext.getService(sfs[0]);
	}

	public <T> T getSingleServiceStrict(Class<T> clss, String filter) {
		T service = getSingleService(clss, filter);
		if (service == null)
			throw new SlcException("No execution flow found for " + filter);
		else
			return service;
	}

	/** @return the related bundle or null if not found */
	public Bundle findRelatedBundle(OsgiBundle osgiBundle) {
		Bundle bundle = null;
		if (osgiBundle.getInternalBundleId() != null) {
			bundle = bundleContext.getBundle(osgiBundle.getInternalBundleId());
			Assert.isTrue(
					osgiBundle.getName().equals(bundle.getSymbolicName()),
					"symbolic name consistent");
			Assert.isTrue(osgiBundle.getVersion().equals(
					bundle.getHeaders().get(Constants.BUNDLE_VERSION)),
					"version consistent");
		} else {
			for (Bundle b : bundleContext.getBundles()) {
				if (b.getSymbolicName().equals(osgiBundle.getName())) {
					if (b.getHeaders().get(Constants.BUNDLE_VERSION).equals(
							osgiBundle.getVersion())) {
						bundle = b;
						osgiBundle.setInternalBundleId(b.getBundleId());
					}
				}
			}
		}
		return bundle;
	}

	/** Find a single bundle based on a symbolic name pattern. */
	public OsgiBundle findFromPattern(String pattern) {
		OsgiBundle osgiBundle = null;
		for (Bundle b : bundleContext.getBundles()) {
			if (b.getSymbolicName().contains(pattern)) {
				osgiBundle = new OsgiBundle(b);
				break;
			}
		}
		return osgiBundle;
	}

	public OsgiBundle getBundle(Long bundleId) {
		Bundle bundle = bundleContext.getBundle(bundleId);
		return new OsgiBundle(bundle);
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void afterPropertiesSet() throws Exception {
		bundleContext.addFrameworkListener(this);
	}

	public void destroy() throws Exception {
		bundleContext.removeFrameworkListener(this);
	}

	public void setDefaultTimeout(Long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	/** Temporary internal access for {@link OsgiExecutionModulesManager} */
	BundleContext getBundleContext() {
		return bundleContext;
	}

}
