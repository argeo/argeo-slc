package org.argeo.slc.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

public class BundlesManager implements BundleContextAware, FrameworkListener,
		InitializingBean {
	private final static Log log = LogFactory.getLog(BundlesManager.class);

	private BundleContext bundleContext;

	private Long defaultTimeout = 10000l;
	private final Object refreshedPackageSem = new Object();

	/** Updates bundle synchronously. */
	public void updateSynchronous(Bundle bundle) throws BundleException {
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
	public void startSynchronous(Bundle bundle) throws BundleException {
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
	public void stopSynchronous(Bundle bundle) throws BundleException {
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
	public void refreshSynchronous(Bundle bundle) throws BundleException {
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

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void afterPropertiesSet() throws Exception {
		bundleContext.addFrameworkListener(this);
	}

}
