package org.argeo.slc.detached;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DetachedExecutionServerImpl implements DetachedExecutionServer {
	private final DetachedContextImpl detachedContext;

	private BundleContext bundleContext;
	private DetachedDriver driver;

	private boolean active = false;

	public void setDriver(DetachedDriver driver) {
		this.driver = driver;
	}

	public DetachedExecutionServerImpl() {
		detachedContext = new DetachedContextImpl();
	}

	public DetachedAnswer executeStep(DetachedRequest request) {
		try {
			DetachedStep step = null;

			// Find step
			ServiceReference[] refs = bundleContext.getAllServiceReferences(
					StaticRefProvider.class.getName(), null);
			for (int i = 0; i < refs.length; i++) {
				StaticRefProvider provider = (StaticRefProvider) bundleContext
						.getService(refs[i]);
				Object obj = provider.getStaticRef(request.getRef());
				if (obj != null) {
					step = (DetachedStep) obj;
					break;
				}
			}

			if (step == null)
				throw new DetachedException("Could not find step with ref "
						+ request.getRef());

			return step.execute(detachedContext, request);
		} catch (DetachedException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DetachedException(
					"Unexpected exception while executing request " + request,
					e);
		}
	}

	public void init(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		Thread driverThread = new Thread(new Runnable() {

			public void run() {
				while (active) {
					try {
						DetachedRequest request = driver.receiveRequest();
						DetachedAnswer answer = executeStep(request);
						driver.sendAnswer(answer);
					} catch (Exception e) {
						if (e instanceof RuntimeException)
							throw (RuntimeException) e;
						else
							e.printStackTrace();
					}
				}

			}
		}, "driverThread");

		active = true;

		driverThread.start();
	}

}
