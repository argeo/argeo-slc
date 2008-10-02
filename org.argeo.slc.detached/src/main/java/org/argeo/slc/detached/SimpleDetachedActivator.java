package org.argeo.slc.detached;

import java.util.Properties;

import org.argeo.slc.detached.drivers.AbstractDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class SimpleDetachedActivator extends AbstractDetachedActivator {
	private AbstractDriver driver;

	protected void startAutoBundle(BundleContext context) throws Exception {
		Object obj = getStaticRefProvider().getStaticRef("slcDetached.driver");
		if (obj != null)
			driver = (AbstractDriver) obj;
		else
			throw new DetachedException("Could not find driver.");

		DetachedExecutionServer executionServer = null;
		ServiceReference ref = context
				.getServiceReference(DetachedExecutionServer.class.getName());
		if (ref != null)
			executionServer = (DetachedExecutionServer) context.getService(ref);
		else
			throw new DetachedException("Could not find execution server.");
		driver.setExecutionServer(executionServer);
		driver.start();

		context.registerService(DetachedDriver.class.getName(), driver,
				new Properties());
	}

	protected void stopAutoBundle(BundleContext context) throws Exception {
		if (driver != null)
			driver.stop();
	}

}
