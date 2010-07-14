/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.log4j;

import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.argeo.slc.core.execution.ExecutionThread;
import org.argeo.slc.core.execution.ProcessThreadGroup;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/** Not meant to be used directly in standard log4j config */
public class SlcExecutionAppender extends AppenderSkeleton implements
		InitializingBean, DisposableBean {

	/** Marker to prevent stack overflow */
	private ThreadLocal<Boolean> dispatching = new ThreadLocal<Boolean>() {

		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	private Layout layout = null;
	private String pattern = "%m - %c%n";
	private Boolean onlyExecutionThread = false;

	public void afterPropertiesSet() {
		if (layout != null)
			setLayout(layout);
		else
			setLayout(new PatternLayout(pattern));
		Logger.getRootLogger().addAppender(this);
	}

	@Override
	protected void append(LoggingEvent event) {
		if (dispatching.get())
			return;

		Thread currentThread = Thread.currentThread();
		if (currentThread.getThreadGroup() instanceof ProcessThreadGroup) {
			if (onlyExecutionThread
					&& !(currentThread instanceof ExecutionThread))
				return;

			final String type;
			if (event.getLevel().equals(Level.ERROR)
					|| event.getLevel().equals(Level.FATAL))
				type = SlcExecutionStep.ERROR;
			else if (event.getLevel().equals(Level.WARN))
				type = SlcExecutionStep.WARNING;
			else if (event.getLevel().equals(Level.INFO))
				type = SlcExecutionStep.INFO;
			else if (event.getLevel().equals(Level.DEBUG))
				type = SlcExecutionStep.DEBUG;
			else if (event.getLevel().equals(Level.TRACE))
				type = SlcExecutionStep.TRACE;
			else
				type = SlcExecutionStep.INFO;

			SlcExecutionStep step = new SlcExecutionStep(new Date(event
					.getTimeStamp()), type, layout.format(event));

			try {
				dispatching.set(true);
				((ProcessThreadGroup) currentThread.getThreadGroup())
						.dispatchAddStep(step);
			} finally {
				dispatching.set(false);
			}
		}
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

	public void setOnlyExecutionThread(Boolean onlyExecutionThread) {
		this.onlyExecutionThread = onlyExecutionThread;
	}

}
