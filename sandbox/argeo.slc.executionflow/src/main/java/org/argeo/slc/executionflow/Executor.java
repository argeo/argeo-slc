package org.argeo.slc.executionflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.process.SlcExecution;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class Executor implements ApplicationListener, ApplicationContextAware {
	private final static Log log = LogFactory.getLog(Executor.class);

	private ApplicationContext applicationContext;

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof NewExecutionEvent) {
			SlcExecution slcExecution = ((NewExecutionEvent) event)
					.getSlcExecution();
			ExecutionContext executionContext = new ExecutionContext();
			ExecutionThread thread = new ExecutionThread(executionContext,
					slcExecution);
			thread.start();
		}

	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	private class ExecutionThread extends Thread {
		private final SlcExecution slcExecution;
		private final ExecutionContext executionContext;

		public ExecutionThread(ExecutionContext executionContext,
				SlcExecution slcExecution) {
			super("SLC Execution #" + executionContext.getUuid());
			this.slcExecution = slcExecution;
			this.executionContext = executionContext;
		}

		public void run() {
			// Initialize from SlcExecution
			ExecutionContext.registerExecutionContext(executionContext);
			ExecutionContext.getVariables()
					.putAll(slcExecution.getAttributes());

			try {
				log
						.info("Start execution #"
								+ ExecutionContext.getExecutionUuid());
				String executionBean = slcExecution.getAttributes().get(
						"slc.flows");
				ExecutionFlow main = (ExecutionFlow) applicationContext
						.getBean(executionBean);
				main.execute();
			} finally {
				applicationContext.publishEvent(new ExecutionFinishedEvent(
						this, executionContext));
			}

		}
	}

}
