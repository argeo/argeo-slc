package org.argeo.slc.ide.ui;

import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
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
		DebugPlugin.getDefault()
				.addDebugEventListener(new DebugEventListener());
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

	protected static class DebugEventListener implements IDebugEventSetListener {
		public void handleDebugEvents(DebugEvent[] events) {
			if (events == null)
				return;

			for (int i = 0; i < events.length; i++) {
				DebugEvent event = events[i];
				if (event == null)
					continue;
				Object source = event.getSource();
				if (source instanceof IProcess
						&& event.getKind() == DebugEvent.TERMINATE) {
					IProcess process = (IProcess) source;
					if (process == null)
						continue;
					ILaunch launch = process.getLaunch();
					if (launch != null)
						refreshOsgiBootLaunch(launch);

				}
			}
		}

		protected void refreshOsgiBootLaunch(ILaunch launch) {
			try {
				if (launch == null)
					return;
				IResource[] resources = launch.getLaunchConfiguration()
						.getMappedResources();
				if (resources == null)
					return;
				if (resources.length > 0) {
					IResource propertiesFile = resources[0];
					if (propertiesFile.getParent() == null)
						return;
					propertiesFile.getParent().refreshLocal(
							IResource.DEPTH_INFINITE, null);
					// System.out.println("Refreshed "
					// + propertiesFile.getParent());
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}

	}

}
