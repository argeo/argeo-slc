package org.argeo.slc.core.execution;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionContext;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionStack;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MapExecutionContext implements ExecutionContext,
		ApplicationContextAware {
	private final Map<String, Object> variables = Collections
			.synchronizedMap(new HashMap<String, Object>());

	private final String uuid;

	private ApplicationContext applicationContext;
	private ExecutionStack executionStack;

	public MapExecutionContext() {
		uuid = UUID.randomUUID().toString();
		variables.put(VAR_EXECUTION_CONTEXT_ID, uuid);
		variables.put(VAR_EXECUTION_CONTEXT_CREATION_DATE, new Date());
	}

	public void setVariable(String key, Object value) {
		// check if we do not refer to a bean
		int lastInd = key.lastIndexOf('.');
		if (applicationContext != null && lastInd > 0) {
			String beanName = key.substring(0, lastInd);
			String propertyName = key.substring(lastInd + 1);
			if (applicationContext.containsBean(beanName)) {
				BeanWrapper beanWrapper = new BeanWrapperImpl(
						applicationContext.getBean(beanName));
				if (!beanWrapper.isWritableProperty(propertyName))
					throw new SlcException("No writable property "
							+ propertyName + " in bean " + beanName);
				beanWrapper.setPropertyValue(propertyName, value);
			}
		}

		variables.put(key, value);
	}

	public Object getVariable(String key) {
		// check if we do not refer to a bean
		int lastInd = key.lastIndexOf('.');
		if (applicationContext != null && lastInd > 0) {
			String beanName = key.substring(0, lastInd);
			String propertyName = key.substring(lastInd + 1);
			if (applicationContext.containsBean(beanName)) {
				BeanWrapper beanWrapper = new BeanWrapperImpl(
						applicationContext.getBean(beanName));
				if (!beanWrapper.isReadableProperty(propertyName))
					throw new SlcException("No readable property "
							+ propertyName + " in bean " + beanName);
				Object obj = beanWrapper.getPropertyValue(propertyName);
				return obj;
			}
		}

		Object value = variables.get(key);
		// try system property in last resort
		if (value == null)
			value = System.getProperty(key);

		// if the variable was not found, look in the stack starting at the
		// upper flows
		if (value == null) {
			value = executionStack.findLocalVariable(key);
		}
		return value;
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public void beforeFlow(ExecutionFlow executionFlow) {
		// getUuid();
		executionStack.enterFlow(executionFlow);
		setVariable(ExecutionContext.VAR_FLOW_ID,
				executionStack.getCurrentStackLevelUuid());
		setVariable(ExecutionContext.VAR_FLOW_NAME, executionFlow.getName());
	}

	@Override
	public void afterFlow(ExecutionFlow executionFlow) {
		executionStack.leaveFlow(executionFlow);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExecutionContext)
			return uuid.equals(((ExecutionContext) obj).getUuid());
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + uuid;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setExecutionStack(ExecutionStack executionStack) {
		this.executionStack = executionStack;
	}

}
