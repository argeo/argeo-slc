package org.argeo.slc.client.ui.dist;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Default activator for the distribution bundle */
public class DistPlugin extends AbstractUIPlugin {
	public final static String PLUGIN_ID = "org.argeo.slc.client.ui.dist";

	private static DistPlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static DistPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
