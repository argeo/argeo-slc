package org.argeo.slc.autoui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AutoUiActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		stdOut("AutoUi started");
	}

	public void stop(BundleContext context) throws Exception {
		stdOut("AutoUi stopped");
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}
}
