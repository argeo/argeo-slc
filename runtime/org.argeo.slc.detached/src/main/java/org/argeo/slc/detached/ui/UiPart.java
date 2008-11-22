package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;

public abstract class UiPart {
	private boolean initialized = false;

	public synchronized final void init(DetachedContext context,
			DetachedRequest request) {
		initUi(context, request);
		initialized = true;
	}

	public synchronized final void reset(DetachedContext context,
			DetachedRequest request) {
		resetUi(context, request);
		initialized = false;
	}

	protected abstract void initUi(DetachedContext context,
			DetachedRequest request);

	protected void resetUi(DetachedContext context, DetachedRequest request) {

	}

	public synchronized boolean isInitialized() {
		return initialized;
	}

}
