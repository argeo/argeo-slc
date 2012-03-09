/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
