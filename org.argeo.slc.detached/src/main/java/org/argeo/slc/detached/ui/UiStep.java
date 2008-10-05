package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedStep;
import org.springframework.beans.factory.BeanNameAware;

public abstract class UiStep implements DetachedStep, BeanNameAware {
	private String beanName;

	public final DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest detachedStepRequest) {
		// uiPart.init(detachedContext, detachedStepRequest);
		return executeUiStep(detachedContext, detachedStepRequest);
	}

	protected abstract DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request);

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public String getBeanName() {
		return beanName;
	}

}
