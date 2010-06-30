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

package org.argeo.slc.core.execution;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionModulesListener;

public class JmxExecutionModulesListeners implements ExecutionModulesListener {
	private String executionModulesPrefix = "SLCExecutionModules";
	private MBeanServer mBeanServer = ManagementFactory
			.getPlatformMBeanServer();

	public void executionModuleAdded(Module module,
			ExecutionContext executionContext) {
	}

	public void executionModuleRemoved(Module module,
			ExecutionContext executionContext) {
	}

	public void executionFlowAdded(Module module, ExecutionFlow executionFlow) {
		try {
			StandardMBean mbean = new StandardMBean(executionFlow,
					ExecutionFlow.class);
			mBeanServer.registerMBean(mbean, flowName(module, executionFlow));
		} catch (Exception e) {
			String msg = "Cannot register execution flow " + executionFlow
					+ " as mbean";
			throw new SlcException(msg, e);
		}
	}

	public void executionFlowRemoved(Module module, ExecutionFlow executionFlow) {
		try {
			mBeanServer.unregisterMBean(flowName(module, executionFlow));
		} catch (Exception e) {
			String msg = "Cannot unregister execution flow " + executionFlow
					+ " as mbean";
			throw new SlcException(msg, e);
		}
	}

	protected ObjectName flowName(Module module, ExecutionFlow executionFlow) {
		String path = executionFlow.getPath();
		String name = executionModulesPrefix + ":" + "module="
				+ module.getName() + "[" + module.getVersion() + "],"
				+ (path != null ? "path=" + path + "," : "") + "name="
				+ executionFlow.getName();
		try {
			return new ObjectName(name);
		} catch (Exception e) {
			throw new SlcException("Cannot generate object name based on "
					+ name, e);
		}
	}
}
