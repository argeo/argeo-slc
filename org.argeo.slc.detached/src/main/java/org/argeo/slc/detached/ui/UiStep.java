package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedStep;

public abstract class UiStep implements DetachedStep {
	private UiPart uiPart;

	public final DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest detachedStepRequest) {
		uiPart.init(detachedContext, detachedStepRequest);
		return executeUiStep(detachedContext, detachedStepRequest);
	}

	protected abstract DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request);

	public UiPart getUiPart() {
		return uiPart;
	}

	public void setUiPart(UiPart uiPart) {
		this.uiPart = uiPart;
	}

}
