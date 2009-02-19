package org.argeo.slc.executionflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.process.Executable;
import org.argeo.slc.test.ExecutableTestRun;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.MapBindingResult;

public class SimpleExecutionFlow implements ExecutionFlow, InitializingBean,
		BeanNameAware {
	private ExecutionSpec executionSpec = new SimpleExecutionSpec();
	private String name = null;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private List<Executable> executables = new ArrayList<Executable>();

	private final String uuid = UUID.randomUUID().toString();

	public void execute() {
		for (Executable executable : executables) {
			executable.execute();
		}
	}

	public void afterPropertiesSet() throws Exception {
		// Validate execution specs
		if (executionSpec == null)
			return;

		MapBindingResult errors = new MapBindingResult(attributes, "execution#"
				+ getUuid());
		for (String key : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute executionSpecAttr = executionSpec
					.getAttributes().get(key);
			if (!attributes.containsKey(key)) {
				Object defaultValue = executionSpecAttr.getValue();
				if (defaultValue == null)
					errors.rejectValue(key, "Not set and no default value");
				else
					attributes.put(key, defaultValue);
			} else {// contains key
				Object obj = attributes.get(key);
				if (executionSpecAttr instanceof RefSpecAttribute) {
					RefSpecAttribute rsa = (RefSpecAttribute) executionSpecAttr;
					Class targetClass = rsa.getTargetClass();
					if (!targetClass.isAssignableFrom(obj.getClass())) {
						errors.reject(key
								+ " not compatible with target class "
								+ targetClass);
					}
				}
			}
		}

		if (errors.hasErrors())
			throw new SlcException("Could not prepare execution flow: "
					+ errors.toString());
	}

	public void setBeanName(String name) {
		this.name = name;
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

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public String getUuid() {
		return uuid;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public String toString() {
		return new StringBuffer("Flow ").append(name).toString();// .append(" [#")
		// .append(uuid).append(']').toString();
	}
}
