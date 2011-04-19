package org.argeo.slc.client.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The activator class controls the plug-in life cycle */
public class ClientUiPlugin extends AbstractUIPlugin {
	public static final String ID = "org.argeo.slc.client.ui";
	private static ClientUiPlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ClientUiPlugin getDefault() {
		return plugin;
	}

	/** Creates the image */
	public static Image img(String path) {
		return getImageDescriptor(path).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}
}
