package org.argeo.slc.autoui.internal;

import org.argeo.slc.autoui.DetachedContextImpl;
import org.argeo.slc.autoui.DetachedException;
import org.argeo.slc.autoui.DetachedExecutionServer;
import org.argeo.slc.autoui.DetachedStep;
import org.argeo.slc.autoui.DetachedStepAnswer;
import org.argeo.slc.autoui.DetachedStepRequest;
import org.argeo.slc.autoui.StaticRefProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DetachedExecutionServerImpl implements DetachedExecutionServer {
	private final DetachedContextImpl detachedContext;

	private BundleContext bundleContext;

	public DetachedExecutionServerImpl() {
		detachedContext = new DetachedContextImpl();
	}

	public DetachedStepAnswer executeStep(DetachedStepRequest request) {
		try {
			DetachedStep step = null;

			// Find step
			ServiceReference[] refs = bundleContext.getAllServiceReferences(
					StaticRefProvider.class.getName(), null);
			for (int i = 0; i < refs.length; i++) {
				StaticRefProvider provider = (StaticRefProvider) bundleContext
						.getService(refs[i]);
				Object obj = provider.getStaticRef(request.getStepRef());
				if (obj != null) {
					step = (DetachedStep) obj;
					break;
				}
			}

			if (step == null)
				throw new DetachedException("Could not find step with ref "
						+ request.getStepRef());

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

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
