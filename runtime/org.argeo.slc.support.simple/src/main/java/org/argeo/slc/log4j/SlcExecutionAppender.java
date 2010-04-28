package org.argeo.slc.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.argeo.slc.core.execution.ExecutionThread;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/** Not meant to be used directly in standard log4j config */
public class SlcExecutionAppender extends AppenderSkeleton implements
		InitializingBean, DisposableBean {

	private Layout layout = null;
	private String pattern = "%m - %c%n";

	public void afterPropertiesSet() {
		if (layout != null)
			setLayout(layout);
		else
			setLayout(new PatternLayout(pattern));
		Logger.getRootLogger().addAppender(this);
	}

	@Override
	protected void append(LoggingEvent event) {
		if (!(Thread.currentThread() instanceof ExecutionThread))
			return;

		ExecutionThread executionThread = (ExecutionThread) Thread
				.currentThread();
		executionThread.dispatchAddStep(new SlcExecutionStep(layout
				.format(event)));
	}

	public void destroy() throws Exception {
		Logger.getRootLogger().removeAppender(this);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}