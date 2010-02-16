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
