package org.argeo.slc.executionflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.argeo.slc.process.Executable;
import org.argeo.slc.test.ExecutableTestRun;
import org.springframework.beans.factory.InitializingBean;

public class SimpleExecutionFlow implements ExecutionFlow, InitializingBean {
	private static ThreadLocal<ExecutionFlow> executionFlow = new ThreadLocal<ExecutionFlow>();

	private ExecutionSpec executionSpec;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private List<Executable> executables = new ArrayList<Executable>();
	
	private final String uuid = UUID.randomUUID().toString();

	public void execute() {
		try {
			executionFlow.set(this);
			for (Executable executable : executables) {
				executable.execute();
			}
		} finally {
			executionFlow.set(null);
		}
	}

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	public void setExecutables(List<Executable> executables) {
		this.executables = executables;
	}

	public void setExecutionSpec(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public static ExecutionFlow getCurrentExecutionFlow() {
		return executionFlow.get();
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public String getUuid() {
		return uuid;
	}

}
