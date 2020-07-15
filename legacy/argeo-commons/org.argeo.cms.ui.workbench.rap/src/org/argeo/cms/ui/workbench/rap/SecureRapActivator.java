package org.argeo.cms.ui.workbench.rap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Configure Equinox login context from the bundle context. */
public class SecureRapActivator implements BundleActivator {
	public final static String ID = "org.argeo.cms.ui.workbench.rap";

	private static BundleContext bundleContext;

	public void start(BundleContext bc) throws Exception {
		bundleContext = bc;
	}

	public void stop(BundleContext context) throws Exception {
		bundleContext = null;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}
}
