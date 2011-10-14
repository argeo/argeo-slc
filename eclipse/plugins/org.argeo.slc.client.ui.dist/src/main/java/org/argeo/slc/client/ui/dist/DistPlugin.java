package org.argeo.slc.client.ui.dist;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DistPlugin extends AbstractUIPlugin {
	private static BundleContext bundleContext;
	public final static String ID = "org.argeo.slc.client.ui.dist";

	private static DistPlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static DistPlugin getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}

}
