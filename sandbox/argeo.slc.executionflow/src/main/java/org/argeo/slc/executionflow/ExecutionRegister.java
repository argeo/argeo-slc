package org.argeo.slc.executionflow;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class ExecutionRegister implements InitializingBean {
	private final static Log log = LogFactory.getLog(ExecutionRegister.class);

	@Autowired
	private Set<ExecutionFlow> executionFlows;

	@Autowired
	private Set<ExecutionSpec> executionSpecs;

	public void afterPropertiesSet() throws Exception {
		log.debug("Register: " + executionSpecs.size() + " specs");
		for (ExecutionSpec spec : executionSpecs) {
			log.debug(spec);
			Map<String, ExecutionSpecAttribute> attributes = spec
					.getAttributes();
			log.debug("Spec attributes: ");
			for (String key : attributes.keySet()) {
				log.debug(" " + key + "\t" + attributes.get(key));
			}
		}

		log.debug("Register: " + executionFlows.size() + " flows");
		for (ExecutionFlow flow : executionFlows) {
			log.debug(flow);
//			Map<String, Object> attributes = flow.getAttributes();
//			log.debug("Specified parameters: ");
//			for (String key : flow.getExecutionSpec().getAttributes().keySet()) {
//				log.debug(" "
//						+ key
//						+ "\t"
//						+ (attributes.containsKey(key) ? "SPECIFIED"
//								: "TO SPECIFY"));
//			}
		}

	}
}
