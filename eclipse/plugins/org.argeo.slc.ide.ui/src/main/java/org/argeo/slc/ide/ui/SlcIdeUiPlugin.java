package org.argeo.slc.ide.ui;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SlcIdeUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String ID = "org.argeo.slc.ide.ui";

	// The shared instance
	private static SlcIdeUiPlugin plugin;

	/**
	 * The constructor
	 */
	public SlcIdeUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SlcIdeUiPlugin getDefault() {
		return plugin;
	}

	public Image getImage(String relativeURL) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(relativeURL);
		if (image == null) {
			URL imageURL = getBundle().getEntry(relativeURL);
			ImageDescriptor descriptor = ImageDescriptor
					.createFromURL(imageURL);
			image = descriptor.createImage();
			imageRegistry.put(relativeURL, image);
		}
		return image;
	}
}
