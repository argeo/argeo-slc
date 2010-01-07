package org.argeo.slc.ide;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SlcIdePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String ID = "org.argeo.slc.ide";

	// The shared instance
	private static SlcIdePlugin plugin;

	/**
	 * The constructor
	 */
	public SlcIdePlugin() {
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
	public static SlcIdePlugin getDefault() {
		return plugin;
	}

}
