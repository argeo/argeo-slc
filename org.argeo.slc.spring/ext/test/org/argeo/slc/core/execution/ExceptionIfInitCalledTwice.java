package org.argeo.slc.core.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.InitializingBean;

public class ExceptionIfInitCalledTwice implements Runnable, InitializingBean {
	private final static Log log = LogFactory
			.getLog(ExceptionIfInitCalledTwice.class);

	private Boolean calledOnce = false;

	public void run() {
		log.info(getClass().getSimpleName() + " ran properly");
	}

	public void afterPropertiesSet() throws Exception {
		log.info(getClass().getSimpleName() + " init method called");

		if (calledOnce)
			throw new SlcException(getClass().getSimpleName()
					+ "init method called twice.");
		else
			calledOnce = true;
	}
}
