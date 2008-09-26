package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.argeo.slc.autoui.AbstractAutoActivator;
import org.argeo.slc.autoui.AutoUiApplication;
import org.osgi.framework.BundleContext;

public class JemmyTestActivator extends AbstractAutoActivator {
	protected void startAutoBundle(BundleContext context) throws Exception {
		Properties properties = new Properties();
		// AutoUiApplicationJemmy applicationJemmy = new
		// AutoUiApplicationJemmy();
		AutoUiApplicationJemmy applicationJemmy = (AutoUiApplicationJemmy) getStaticRef("jemmyTest");
		context.registerService(AutoUiApplication.class.getName(),
				applicationJemmy, properties);
		stdOut("JemmyTest started");
	}

	public void stopAutoBundle(BundleContext context) throws Exception {
		stdOut("JemmyTest stopped");
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}

}
