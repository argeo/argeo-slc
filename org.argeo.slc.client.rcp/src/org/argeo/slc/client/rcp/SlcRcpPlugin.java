package org.argeo.slc.client.rcp;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SlcRcpPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.argeo.slc.client.rcp";
	private static SlcRcpPlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static SlcRcpPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		// TODO make it configurable
		reg.put("argeoTrayIcon",
				getImageDescriptor("icons/argeo-trayIcon-256.png"));
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
