package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.dbunit.operation.UpdateOperation;
import org.springframework.util.Assert;

public class DefaultModulesManager implements ExecutionModulesManager {
	private final static Log log = LogFactory
			.getLog(DefaultModulesManager.class);

	private List<ExecutionModule> executionModules = new ArrayList<ExecutionModule>();
	private List<SlcExecutionNotifier> slcExecutionNotifiers = new ArrayList<SlcExecutionNotifier>();

	protected ExecutionModule getExecutionModule(String moduleName,
			String version) {
		for (ExecutionModule moduleT : executionModules) {
			if (moduleT.getName().equals(moduleName)) {
				if (moduleT.getVersion().equals(version)) {
					return moduleT;
				}
			}
		}
		return null;
	}

	public ExecutionModuleDescriptor getExecutionModuleDescriptor(
			String moduleName, String version) {
		ExecutionModule module = getExecutionModule(moduleName, version);

		Assert.notNull(module);

		return module.getDescriptor();
	}

	public List<ExecutionModule> listExecutionModules() {
		return executionModules;
	}

	public void setExecutionModules(List<ExecutionModule> executionModules) {
		this.executionModules = executionModules;
	}

	protected Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor) {
		// convert the values of flow.getFlowDescriptor()
		Map<String, Object> values = executionFlowDescriptor.getValues();

		Map<String, Object> convertedValues = new HashMap<String, Object>();

		for (String key : values.keySet()) {
			Object value = values.get(key);
			if (value instanceof PrimitiveValue) {
				PrimitiveValue primitiveValue = (PrimitiveValue) value;

				// TODO: check that the class of the the primitiveValue.value
				// matches
				// the primitiveValue.type
				convertedValues.put(key, primitiveValue.getValue());
			} else if (value instanceof RefValue) {
				RefValue refValue = (RefValue) value;
				convertedValues.put(key, refValue.getLabel());
			}
		}
		return convertedValues;
	}

	public void process(SlcExecution slcExecution) {
		log.info("\n##\n## Process SLC Execution " + slcExecution + "\n##\n");

		for (RealizedFlow flow : slcExecution.getRealizedFlows()) {
			ExecutionModule module = getExecutionModule(flow.getModuleName(),
					flow.getModuleVersion());
			if (module != null) {
				ExecutionThread thread = new ExecutionThread(flow
						.getFlowDescriptor(), module);
				thread.start();
			} else {
				throw new SlcException("ExecutionModule "
						+ flow.getModuleName() + ", version "
						+ flow.getModuleVersion() + " not found.");
			}
		}

		slcExecution.setStatus(SlcExecution.STATUS_RUNNING);
		dispatchUpdateStatus(slcExecution, SlcExecution.STATUS_SCHEDULED,
				SlcExecution.STATUS_RUNNING);
	}

	protected void dispatchUpdateStatus(SlcExecution slcExecution,
			String oldStatus, String newStatus) {
		for (Iterator<SlcExecutionNotifier> it = slcExecutionNotifiers
				.iterator(); it.hasNext();) {
			it.next().updateStatus(slcExecution, oldStatus, newStatus);
		}
	}

	public void setSlcExecutionNotifiers(
			List<SlcExecutionNotifier> slcExecutionNotifiers) {
		this.slcExecutionNotifiers = slcExecutionNotifiers;
	}

	private class ExecutionThread extends Thread {
		private final ExecutionFlowDescriptor executionFlowDescriptor;
		private final ExecutionModule executionModule;

		public ExecutionThread(ExecutionFlowDescriptor executionFlowDescriptor,
				ExecutionModule executionModule) {
			super("SLC Execution #" /* + executionContext.getUuid() */);
			this.executionFlowDescriptor = executionFlowDescriptor;
			this.executionModule = executionModule;
		}

		public void run() {
			ExecutionContext executionContext = executionModule
					.getExecutionContext();
			executionContext
					.addVariables(convertValues(executionFlowDescriptor));
			try {
				executionModule.execute(executionFlowDescriptor);
			} catch (Exception e) {
				// TODO: re-throw exception ?
				log.error("Execution " + executionContext.getUuid()
						+ " failed.", e);
			}
		}
	}
}
