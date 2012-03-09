/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.detached.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Wraps low-level access to a {@link BundleContext}. Hacked from the related
 * class in org.argeo.slc.support.osgi.
 */
class MinimalBundlesManager implements FrameworkListener {
	private final static Log log = LogFactory
			.getLog(MinimalBundlesManager.class);

	private final BundleContext bundleContext;

	private long defaultTimeout = 10000l;
	private final Object refreshedPackageSem = new Object();

	public MinimalBundlesManager(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		bundleContext.addFrameworkListener(this);
	}

	protected void finalize() throws Throwable {
		bundleContext.removeFrameworkListener(this);
	}

	/**
	 * @see #upgradeSynchronous(Bundle[])
	 */
	public void upgradeSynchronous(Bundle bundle) {
		upgradeSynchronous(new Bundle[] { bundle });
	}

	/**
	 * Stop the active bundles, update them, refresh them and restart the
	 * initially active bundles. All synchronously.
	 */
	public void upgradeSynchronous(Bundle[] bundles) {
		try {
			// State (ACTIVE or other) before upgrading
			int[] initialStates = new int[bundles.length];

			// store initial state and stop active bundles
			for (int i = 0; i < bundles.length; ++i) {
				initialStates[i] = bundles[i].getState();
				if (initialStates[i] == Bundle.ACTIVE) {
					stopSynchronous(bundles[i]);
				}
			}

			// update the bundles
			for (int i = 0; i < bundles.length; ++i) {
				updateSynchronous(bundles[i]);
			}

			// refresh the bundles
			refreshSynchronous(bundles);

			// restart the bundles that were ACTIVE before upgrading
			for (int i = 0; i < bundles.length; ++i) {
				if (initialStates[i] == Bundle.ACTIVE) {
					startSynchronous(bundles[i]);

					String filter = "(Bundle-SymbolicName="
							+ bundles[i].getSymbolicName() + ")";
					// Wait for application context to be ready
					// TODO: use service tracker
					try {
						getServiceRefSynchronous(
								"org.springframework.context.ApplicationContext",
								filter);
					}
					// in case of exception, catch and go on
					catch (Exception e) {
						log.error("getServiceRefSynchronous failed", e);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot update bundles", e);
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

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new RuntimeException("Update of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isDebugEnabled())
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
				throw new RuntimeException("Start of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isDebugEnabled())
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
				throw new RuntimeException("Stop of bundle "
						+ bundle.getSymbolicName()
						+ " timed out. Bundle state = " + bundle.getState());
		} while (waiting);

		if (log.isDebugEnabled())
			log.debug("Bundle " + bundle.getSymbolicName() + " stopped.");
	}

	/** Refresh bundle synchronously. Does nothing if already started. */
	protected void refreshSynchronous(Bundle[] bundles) throws BundleException {
		ServiceReference packageAdminRef = bundleContext
				.getServiceReference(PackageAdmin.class.getName());
		PackageAdmin packageAdmin = (PackageAdmin) bundleContext
				.getService(packageAdminRef);
		packageAdmin.refreshPackages(bundles);

		synchronized (refreshedPackageSem) {
			try {
				refreshedPackageSem.wait(defaultTimeout);
			} catch (InterruptedException e) {
				// silent
			}
		}

		if (log.isDebugEnabled())
			log.debug("Bundles refreshed.");
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
			log.trace("Filter: '" + filter + "'");
		ServiceReference[] sfs = null;
		boolean waiting = true;
		long begin = System.currentTimeMillis();
		do {
			sfs = bundleContext.getServiceReferences(clss, filter);

			if (sfs != null)
				waiting = false;

			sleep(100);
			if (System.currentTimeMillis() - begin > defaultTimeout)
				throw new RuntimeException("Search of services " + clss
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

}
