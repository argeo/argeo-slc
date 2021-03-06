package org.argeo.slc.osgi;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
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
import org.springframework.util.Assert;

/** Wraps low-level access to a {@link BundleContext} */
@SuppressWarnings("deprecation")
public class BundlesManager implements BundleContextAware, FrameworkListener,
		InitializingBean, DisposableBean,
		OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent> {
	private final static Log log = LogFactory.getLog(BundlesManager.class);

	private BundleContext bundleContext;

	private Long defaultTimeout = 60 * 1000l;
	private Long pollingPeriod = 200l;

	// Refresh sync objects
	private final Object refreshedPackageSem = new Object();
	private Boolean packagesRefreshed = false;

	public BundlesManager() {
	}

	public BundlesManager(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * Stop the module, update it, refresh it and restart it. All synchronously.
	 */
	public void upgradeSynchronous(OsgiBundle osgiBundle) {
		try {
			Bundle bundle = findRelatedBundle(osgiBundle);

			long begin = System.currentTimeMillis();

			long bStop = begin;
			stopSynchronous(bundle);

			long bUpdate = System.currentTimeMillis();
			updateSynchronous(bundle);

			// Refresh in case there are fragments
			long bRefresh = System.currentTimeMillis();
			refreshSynchronous(bundle);

			long bStart = System.currentTimeMillis();
			startSynchronous(bundle);

			long aStart = System.currentTimeMillis();
			if (log.isTraceEnabled()) {
				log.debug("OSGi upgrade performed in " + (aStart - begin)
						+ "ms for bundle " + osgiBundle);
				log.debug(" stop \t: " + (bUpdate - bStop) + "ms");
				log.debug(" update\t: " + (bRefresh - bUpdate) + "ms");
				log.debug(" refresh\t: " + (bStart - bRefresh) + "ms");
				log.debug(" start\t: " + (aStart - bStart) + "ms");
				log.debug(" TOTAL\t: " + (aStart - begin) + "ms");
			}

			long bAppContext = System.currentTimeMillis();
			String filter = "(Bundle-SymbolicName=" + bundle.getSymbolicName()
					+ ")";
			// Wait for application context to be ready
			// TODO: use service tracker
			Collection<ServiceReference<ApplicationContext>> srs = getServiceRefSynchronous(
					ApplicationContext.class, filter);
			ServiceReference<ApplicationContext> sr = srs.iterator().next();
			long aAppContext = System.currentTimeMillis();
			long end = aAppContext;

			if (log.isTraceEnabled()) {
				log.debug("Application context refresh performed in "
						+ (aAppContext - bAppContext) + "ms for bundle "
						+ osgiBundle);
			}

			if (log.isDebugEnabled())
				log.debug("Bundle '" + bundle.getSymbolicName()
						+ "' upgraded and ready " + " (upgrade performed in "
						+ (end - begin) + "ms).");

			if (log.isTraceEnabled()) {
				ApplicationContext applicationContext = (ApplicationContext) bundleContext
						.getService(sr);
				int beanDefCount = applicationContext.getBeanDefinitionCount();
				log.debug(" " + beanDefCount + " beans in app context of "
						+ bundle.getSymbolicName()
						+ ", average init time per bean=" + (end - begin)
						/ beanDefCount + "ms");
			}

			bundleContext.ungetService(sr);

		} catch (Exception e) {
			throw new SlcException("Cannot update bundle " + osgiBundle, e);
		}
	}

	/** Updates bundle synchronously. */
	protected void updateSynchronous(Bundle bundle) throws BundleException {
		bundle.update();
		boolean waiting = true;

		long begin = System.currentTimeMillis();
		do {
			int state = bundle.getState();
			if (state == Bundle.INSTALLED || state == Bundle.ACTIVE
					|| state == Bundle.RESOLVED)
				waiting = false;

			sleepWhenPolling();
			checkTimeout(begin, "Update of bundle " + bundle.getSymbolicName()
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

			sleepWhenPolling();
			checkTimeout(begin, "Start of bundle " + bundle.getSymbolicName()
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

			sleepWhenPolling();
			checkTimeout(begin, "Stop of bundle " + bundle.getSymbolicName()
					+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " stopped.");
	}

	/** Refresh bundle synchronously. Does nothing if already started. */
	protected void refreshSynchronous(Bundle bundle) throws BundleException {
		ServiceReference<PackageAdmin> packageAdminRef = bundleContext
				.getServiceReference(PackageAdmin.class);
		PackageAdmin packageAdmin = (PackageAdmin) bundleContext
				.getService(packageAdminRef);
		Bundle[] bundles = { bundle };

		long begin = System.currentTimeMillis();
		synchronized (refreshedPackageSem) {
			packagesRefreshed = false;
			packageAdmin.refreshPackages(bundles);
			try {
				refreshedPackageSem.wait(defaultTimeout);
			} catch (InterruptedException e) {
				// silent
			}
			if (!packagesRefreshed) {
				long now = System.currentTimeMillis();
				throw new SlcException("Packages not refreshed after "
						+ (now - begin) + "ms");
			} else {
				packagesRefreshed = false;
			}
		}

		if (log.isTraceEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " refreshed.");
	}

	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
			synchronized (refreshedPackageSem) {
				packagesRefreshed = true;
				refreshedPackageSem.notifyAll();
			}
		}
	}

	public <S> Collection<ServiceReference<S>> getServiceRefSynchronous(
			Class<S> clss, String filter) throws InvalidSyntaxException {
		if (log.isTraceEnabled())
			log.debug("Filter: '" + filter + "'");
		Collection<ServiceReference<S>> sfs = null;
		boolean waiting = true;
		long begin = System.currentTimeMillis();
		do {
			sfs = bundleContext.getServiceReferences(clss, filter);

			if (sfs != null)
				waiting = false;

			sleepWhenPolling();
			checkTimeout(begin, "Search of services " + clss + " with filter "
					+ filter + " timed out.");
		} while (waiting);

		return sfs;
	}

	protected void checkTimeout(long begin, String msg) {
		long now = System.currentTimeMillis();
		if (now - begin > defaultTimeout)
			throw new SlcException(msg + " (timeout after " + (now - begin)
					+ "ms)");

	}

	protected void sleepWhenPolling() {
		try {
			Thread.sleep(pollingPeriod);
		} catch (InterruptedException e) {
			throw new SlcException("Polling interrupted");
		}
	}

	/** Creates and open a new service tracker. */
	public <S> ServiceTracker<S, S> newTracker(Class<S> clss) {
		ServiceTracker<S, S> st = new ServiceTracker<S, S>(bundleContext, clss,
				null);
		st.open();
		return st;
	}

	public <T> T getSingleService(Class<T> clss, String filter,
			Boolean synchronous) {
		if (filter != null)
			Assert.isTrue(OsgiFilterUtils.isValidFilter(filter), "valid filter");
		Collection<ServiceReference<T>> sfs;
		try {
			if (synchronous)
				sfs = getServiceRefSynchronous(clss, filter);
			else
				sfs = bundleContext.getServiceReferences(clss, filter);
		} catch (InvalidSyntaxException e) {
			throw new SlcException("Cannot retrieve service reference for "
					+ filter, e);
		}

		if (sfs == null || sfs.size() == 0)
			return null;
		else if (sfs.size() > 1)
			throw new SlcException("More than one execution flow found for "
					+ filter);
		return (T) bundleContext.getService(sfs.iterator().next());
	}

	public <T> T getSingleServiceStrict(Class<T> clss, String filter,
			Boolean synchronous) {
		T service = getSingleService(clss, filter, synchronous);
		if (service == null)
			throw new SlcException("No execution flow found for " + filter);
		else
			return service;
	}

	public OsgiBundle findRelatedBundle(String moduleName, String moduleVersion) {
		OsgiBundle osgiBundle = new OsgiBundle(moduleName, moduleVersion);
		if (osgiBundle.getVersion() == null) {
			Bundle bundle = findRelatedBundle(osgiBundle);
			osgiBundle = new OsgiBundle(bundle);
		}
		return osgiBundle;
	}

	/**
	 * @param osgiBundle
	 *            cannot be null
	 * @return the related bundle or null if not found
	 * @throws SlcException
	 *             if osgiBundle argument is null
	 */
	public Bundle findRelatedBundle(OsgiBundle osgiBundle) {
		if (osgiBundle == null)
			throw new SlcException("OSGi bundle cannot be null");

		Bundle bundle = null;
		if (osgiBundle.getInternalBundleId() != null) {
			bundle = bundleContext.getBundle(osgiBundle.getInternalBundleId());
			Assert.isTrue(
					osgiBundle.getName().equals(bundle.getSymbolicName()),
					"symbolic name consistent");
			if (osgiBundle.getVersion() != null)
				Assert.isTrue(
						osgiBundle.getVersion().equals(
								bundle.getHeaders().get(
										Constants.BUNDLE_VERSION)),
						"version consistent");
		} else if (osgiBundle.getVersion() == null
				|| osgiBundle.getVersion().equals("0.0.0")) {
			bundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
					osgiBundle.getName());
		} else {// scan all bundles
			bundles: for (Bundle b : bundleContext.getBundles()) {
				if (b.getSymbolicName() == null) {
					log.warn("Bundle " + b + " has no symbolic name defined.");
					continue bundles;
				}

				if (b.getSymbolicName().equals(osgiBundle.getName())) {
					if (osgiBundle.getVersion() == null) {
						bundle = b;
						break bundles;
					}

					if (b.getHeaders().get(Constants.BUNDLE_VERSION)
							.equals(osgiBundle.getVersion())) {
						bundle = b;
						osgiBundle.setInternalBundleId(b.getBundleId());
						break bundles;
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

	/**
	 * Use with caution since it may interfer with some cached information
	 * within this object
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setPollingPeriod(Long pollingPeriod) {
		this.pollingPeriod = pollingPeriod;
	}

	public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
		if (event instanceof OsgiBundleContextRefreshedEvent) {
			log.debug("App context refreshed: " + event);
		} else if (event instanceof OsgiBundleContextFailedEvent) {
			log.debug("App context failed: " + event);
		}
		if (event instanceof OsgiBundleContextClosedEvent) {
			log.debug("App context closed: " + event);
		}

	}

}
