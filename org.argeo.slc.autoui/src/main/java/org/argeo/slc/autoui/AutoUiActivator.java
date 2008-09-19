package org.argeo.slc.autoui;

import java.util.Properties;

import org.argeo.slc.autoui.internal.AutoUiApplicationJemmy;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AutoUiActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		stdOut("AutoUi started");
		Properties properties = new Properties();
		AutoUiApplicationJemmy applicationJemmy = new AutoUiApplicationJemmy();
		context.registerService(AutoUiApplication.class.getName(),
				applicationJemmy, properties);
		context.registerService(Runnable.class.getName(), applicationJemmy,
				properties);
	}

	public void stop(BundleContext context) throws Exception {
		stdOut("AutoUi stopped");
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}
}
