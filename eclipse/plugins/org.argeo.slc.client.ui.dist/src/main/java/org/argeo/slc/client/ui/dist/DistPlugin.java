package org.argeo.slc.client.ui.dist;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DistPlugin extends AbstractUIPlugin {
	private static BundleContext bundleContext;

	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}
