package org.argeo.slc.autoui;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.autoui.internal.DetachedExecutionServerImpl;
import org.osgi.framework.BundleContext;

public class AutoUiActivator extends AbstractDetachedActivator {
	private final Log log = LogFactory.getLog(getClass());

	private DetachedExecutionServerImpl executionServer;

	public void startAutoBundle(BundleContext context) throws Exception {
		Object obj = getStaticRefProvider().getStaticRef("executionServer");
		if (obj != null)
			executionServer = (DetachedExecutionServerImpl) obj;
		else
			throw new DetachedException("Could not find execution server.");

		executionServer.setBundleContext(context);

		context.registerService(DetachedExecutionServer.class.getName(),
				executionServer, new Properties());
		log.info("AutoUi started");
	}

	public void stopAutoBundle(BundleContext context) throws Exception {
		log.info("AutoUi stopped");
	}
}
