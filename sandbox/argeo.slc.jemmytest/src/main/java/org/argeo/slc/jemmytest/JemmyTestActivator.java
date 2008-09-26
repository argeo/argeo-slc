package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.argeo.slc.autoui.AbstractDetachedActivator;
import org.argeo.slc.autoui.DetachedStep;
import org.osgi.framework.BundleContext;

public class JemmyTestActivator extends AbstractDetachedActivator {
	protected void startAutoBundle(BundleContext context) throws Exception {
		Properties properties = new Properties();
		DummyStep applicationJemmy = (DummyStep) getStaticRef("jemmyTest");
		context.registerService(DetachedStep.class.getName(),
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
