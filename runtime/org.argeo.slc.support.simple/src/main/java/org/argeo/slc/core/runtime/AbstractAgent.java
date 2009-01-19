package org.argeo.slc.core.runtime;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcApplication;
import org.argeo.slc.runtime.SlcExecutionContext;

public abstract class AbstractAgent {
	private final static Log log = LogFactory.getLog(AbstractAgent.class);

	private SlcApplication<SlcExecutionContext> slcApplication;

	protected void runSlcExecution(final SlcExecution slcExecution) {
		// TODO: in a separate process
		Thread thread = new Thread("SlcExecution " + slcExecution.getUuid()) {
			public void run() {
				Properties props = new Properties();
				Map<String, String> attributes = slcExecution.getAttributes();
				for (String key : attributes.keySet()) {
					props.setProperty(key, attributes.get(key));
					if (log.isTraceEnabled())
						log.trace(key + "=" + props.getProperty(key));
				}
				slcApplication.execute(slcExecution, props, null, null);
				log.debug("Thread for SLC execution #" + slcExecution.getUuid()
						+ " finished.");
			}
		};
		thread.start();
	}

	public void setSlcApplication(
			SlcApplication<SlcExecutionContext> application) {
		this.slcApplication = application;
	}

}
