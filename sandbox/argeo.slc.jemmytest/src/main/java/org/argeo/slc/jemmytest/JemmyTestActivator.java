package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.argeo.slc.autoui.AutoUiApplication;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class JemmyTestActivator implements BundleActivator, ServiceListener {

	public void start(BundleContext context) throws Exception {
		stdOut("JemmyTest started");
		Properties properties = new Properties();
		AutoUiApplicationJemmy applicationJemmy = new AutoUiApplicationJemmy();
		context.registerService(AutoUiApplication.class.getName(),
				applicationJemmy, properties);
		context.registerService(Runnable.class.getName(), applicationJemmy,
				properties);

//		ServiceReference ref = context
//				.getServiceReference("org.argeo.slc.autoui.AutoUiApplication");
//		Object service = context.getService(ref);
//		// JemmyTestActivator.stdOut("service=" + service.getClass());
//		AutoUiApplication app = (AutoUiApplication) service;
//		app.execute(null);

	}

	public void stop(BundleContext context) throws Exception {
		stdOut("JemmyTest stopped");
	}

	public void serviceChanged(ServiceEvent serviceEvent) {
		stdOut("serviceEvent=" + serviceEvent);

	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}

}
