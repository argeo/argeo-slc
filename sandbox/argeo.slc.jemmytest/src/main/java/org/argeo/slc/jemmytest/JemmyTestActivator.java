package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.argeo.slc.autoui.AutoUiApplication;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class JemmyTestActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Properties properties = new Properties();
		AutoUiApplicationJemmy applicationJemmy = new AutoUiApplicationJemmy();
		context.registerService(AutoUiApplication.class.getName(),
				applicationJemmy, properties);
		stdOut("JemmyTest started");
	}

	public void stop(BundleContext context) throws Exception {
		stdOut("JemmyTest stopped");
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}

}
