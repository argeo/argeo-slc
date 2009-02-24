package org.argeo.slc.execution;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.BeanNameAware;

public class SimpleExecutionSpec implements ExecutionSpec, BeanNameAware {
	private final static Log log = LogFactory.getLog(SimpleExecutionSpec.class);

	private final static ThreadLocal<ExecutionFlow> initializingFlow = new ThreadLocal<ExecutionFlow>();

	private Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

	private String name = null;

	public Map<String, ExecutionSpecAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ExecutionSpecAttribute> attributes) {
		this.attributes = attributes;
	}

	public Object createRef(String name) {
		ExecutionFlow flow = initializingFlow.get();
		if (flow == null)
			throw new SlcException("No flow is currently initializing."
					+ " Declare flow refs as inner beans or prototypes.");
		/*
		 * RefSpecAttribute refSpecAttribute = (RefSpecAttribute) attributes
		 * .get(name); Class<?> targetClass = refSpecAttribute.getTargetClass();
		 * ExecutionTargetSource targetSource = new ExecutionTargetSource(flow,
		 * targetClass, name); ProxyFactory proxyFactory = new ProxyFactory();
		 * proxyFactory.setTargetClass(targetClass);
		 * proxyFactory.setProxyTargetClass(true);
		 * proxyFactory.setTargetSource(targetSource);
		 * 
		 * return proxyFactory.getProxy();
		 */
		return flow.getParameter(name);
	}

	public void setBeanName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// FLOWS INITIALIZATION SUPPORT

	public static void flowInitializationStarted(ExecutionFlow flow) {
		if (log.isTraceEnabled())
			log.trace("Start initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");
		initializingFlow.set(flow);
	}

	public static void flowInitializationFinished(ExecutionFlow flow) {
		if (log.isTraceEnabled())
			log.trace("Finish initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");
		ExecutionFlow registeredFlow = initializingFlow.get();
		if (registeredFlow != null) {
			if (!flow.getName().equals(registeredFlow.getName()))
				throw new SlcException("Current flow is " + flow);
			initializingFlow.set(null);
		}
	}

	public static Object getInitializingFlowParameter(String key) {
		if (initializingFlow.get() == null)
			throw new SlcException("No initializing flow available.");
		return initializingFlow.get().getParameter(key);
	}

	public static Boolean isInFlowInitialization() {
		return initializingFlow.get() != null;
	}

	public boolean equals(Object obj) {
		return ((ExecutionSpec) obj).getName().equals(name);
	}

}
