package org.argeo.slc.core.execution;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlow;

public class InstantiationManager {

	private final static Log log = LogFactory.getLog(InstantiationManager.class);
	
	private ThreadLocal<Stack<ExecutionFlow> > flowStack = new ThreadLocal<Stack<ExecutionFlow> >();
	
	public Object createRef(String name) {
		if((flowStack.get() == null) ||  flowStack.get().empty()) {
			throw new SlcException("No flow is currently initializing."
					+ " Declare flow refs as inner beans or prototypes.");
		}

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
		return flowStack.get().peek().getParameter(name);
	}	
	
	public void flowInitializationStarted(ExecutionFlow flow, String flowName) {
		if (log.isTraceEnabled())
			log.trace("Start initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");
//		log.info("# flowInitializationStarted " + flowName);
		// create a stack for this thread if there is none
		if(flowStack.get() == null) {
			flowStack.set(new Stack<ExecutionFlow>());
		}
		flowStack.get().push(flow);
	}

	public void flowInitializationFinished(ExecutionFlow flow, String flowName) {
		if (log.isTraceEnabled())
			log.trace("Finish initialization of " + flow.hashCode() + " ("
					+ flow + " - " + flow.getClass() + ")");
		ExecutionFlow registeredFlow = flowStack.get().pop();
		if (registeredFlow != null) {
			if (!flow.getName().equals(registeredFlow.getName()))
				throw new SlcException("Current flow is " + flow);
//			log.info("# flowInitializationFinished " + flowName);
//			initializingFlow.set(null);
		}
	}	
	
	public Object getInitializingFlowParameter(String key) {
		if ((flowStack.get() == null) || flowStack.get().empty())
			throw new SlcException("No initializing flow available.");
		
		// first look in the outer flow (that may override parameters)
		for(int i = 0; i < flowStack.get().size(); i++) {
			if(flowStack.get().elementAt(i).isSetAsParameter(key)) {
				return flowStack.get().elementAt(i).getParameter(key);
			}
		}
		throw new SlcException("Key " + key + " is not set as parameter in "
				+ flowStack.get().firstElement().toString());
		
//		return flowStack.get().peek().getParameter(key);
	}

	public Boolean isInFlowInitialization() {
		return (flowStack.get() != null) && !flowStack.get().empty();
	}	
}
