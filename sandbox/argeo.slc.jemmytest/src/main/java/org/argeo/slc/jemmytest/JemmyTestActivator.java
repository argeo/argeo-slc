package org.argeo.slc.jemmytest;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.autoui.AbstractDetachedActivator;
import org.argeo.slc.autoui.DetachedStep;
import org.osgi.framework.BundleContext;

public class JemmyTestActivator extends AbstractDetachedActivator {
	private final Log log = LogFactory.getLog(getClass());
	
	protected void startAutoBundle(BundleContext context) throws Exception {
//		Properties properties = new Properties();
//		DummyStep applicationJemmy = (DummyStep) getStaticRef("jemmyTest");
//		context.registerService(DetachedStep.class.getName(),
//				applicationJemmy, properties);
		log.info("JemmyTest started");
	}

	public void stopAutoBundle(BundleContext context) throws Exception {
		log.info("JemmyTest stopped");
	}
}
