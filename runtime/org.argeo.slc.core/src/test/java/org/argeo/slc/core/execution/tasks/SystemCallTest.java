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
package org.argeo.slc.core.execution.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.AbstractExecutionFlowTestCase;

public class SystemCallTest extends AbstractExecutionFlowTestCase {
	private final static Log log = LogFactory.getLog(SystemCallTest.class);

	private final String defFile = "systemCall.xml";

	public void testSystemCallSimple() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallSimple");
	}

	public void testSystemCallList() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallList");
	}

	public void testSystemCallOsSpecific() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallOsSpecific");
	}

	public void testSystemCallWithVar() throws Exception {
		if (isOsSupported())
			configureAndExecuteSlcFlow(defFile, "systemCallWithVar");
	}

	protected boolean isOsSupported() {
		String osName = System.getProperty("os.name");
		final Boolean ret;
		if (osName.contains("Windows"))
			ret = false;
		else
			ret = true;

		if (ret == false)
			log.warn("Skip test because OS '" + osName + "' is not supported.");
		return ret;
	}
}
