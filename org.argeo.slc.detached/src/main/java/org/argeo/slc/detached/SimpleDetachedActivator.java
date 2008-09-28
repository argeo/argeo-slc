package org.argeo.slc.detached;

import java.util.Properties;

import org.osgi.framework.BundleContext;

public class SimpleDetachedActivator extends AbstractDetachedActivator {
	private DetachedExecutionServerImpl executionServer;

	protected void startAutoBundle(BundleContext context) throws Exception {
		Object obj = getStaticRefProvider().getStaticRef("executionServer");
		if (obj != null)
			executionServer = (DetachedExecutionServerImpl) obj;
		else
			throw new DetachedException("Could not find execution server.");

		executionServer.init(context);

		context.registerService(DetachedExecutionServer.class.getName(),
				executionServer, new Properties());
	}

}
