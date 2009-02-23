package org.argeo.slc.execution;

import org.argeo.slc.process.SlcExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class TestLaunch implements ApplicationEventPublisherAware {
	private ApplicationEventPublisher applicationEventPublisher;

	private String flowName;

	public void launch() {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put("slc.flows", flowName);
		applicationEventPublisher.publishEvent(new NewExecutionEvent(this,
				slcExecution));

	}

	@Required
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

}
