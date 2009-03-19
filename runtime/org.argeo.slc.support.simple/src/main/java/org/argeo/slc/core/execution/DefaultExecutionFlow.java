package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.execution.Executable;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureRegistry;
import org.argeo.slc.test.ExecutableTestRun;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.MapBindingResult;

public class DefaultExecutionFlow implements ExecutionFlow, InitializingBean,
		BeanNameAware {
	private ExecutionSpec executionSpec = new DefaultExecutionSpec();
	private String name = null;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Executable> executables = new ArrayList<Executable>();

	private String path;
	private StructureRegistry<TreeSPath> registry = new TreeSRegistry();

	public DefaultExecutionFlow() {

	}

	public DefaultExecutionFlow(Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
	}

	public void execute() {
		for (Executable executable : executables) {
			executable.execute();
		}
	}

	public void afterPropertiesSet() throws Exception {
		// Validate execution specs
		if (executionSpec == null)
			return;

		MapBindingResult errors = new MapBindingResult(parameters, "execution#"
				+ getName());
		for (String key : executionSpec.getAttributes().keySet()) {
			ExecutionSpecAttribute attr = executionSpec.getAttributes()
					.get(key);

			if (attr.getIsParameter() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Parameter not set");
				break;
			}

			if (attr.getIsFrozen() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Frozen but not set as parameter");
				break;
			}

			if (attr.getIsHidden() && !isSetAsParameter(key)) {
				errors.rejectValue(key, "Hidden but not set as parameter");
				break;
			}

			/*
			 * if (!parameters.containsKey(key)) { Object defaultValue =
			 * attr.getValue(); if (defaultValue == null)
			 * errors.rejectValue(key, "Not set and no default value"); else
			 * parameters.put(key, defaultValue); } else {// contains key Object
			 * obj = parameters.get(key); if (attr instanceof RefSpecAttribute)
			 * { RefSpecAttribute rsa = (RefSpecAttribute) attr; // TODO: make
			 * sure this will not cause pb with OSGi Class targetClass =
			 * rsa.getTargetClass(); if
			 * (!targetClass.isAssignableFrom(obj.getClass())) {
			 * errors.reject(key + " not compatible with target class " +
			 * targetClass); } } }
			 */
		}

		if (errors.hasErrors())
			throw new SlcException("Could not prepare execution flow: "
					+ errors.toString());

		if (path == null) {
			path = "/" + executionSpec.getName() + "/" + name;
		}

		for (Executable executable : executables) {
			if (executable instanceof StructureAware) {
				((StructureAware<TreeSPath>) executable).notifyCurrentPath(
						registry, new TreeSPath(path));
			}
		}
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

	public void setParameters(Map<String, Object> attributes) {
		this.parameters = attributes;
	}

	public String getName() {
		return name;
	}

	public ExecutionSpec getExecutionSpec() {
		return executionSpec;
	}

	public Object getParameter(String name) {
		if (parameters.containsKey(name)) {
			return parameters.get(name);
		} else {
			if (executionSpec.getAttributes().containsKey(name)) {
				ExecutionSpecAttribute esa = executionSpec.getAttributes().get(
						name);
				if (esa.getValue() != null)
					return esa.getValue();
			} else {
				throw new SlcException("Key " + name
						+ " is not defined in the specifications of "
						+ toString());
			}
		}
		throw new SlcException("Key " + name + " is not set as parameter in "
				+ toString());
	}

	public Boolean isSetAsParameter(String key) {
		return parameters.containsKey(key)
				|| (executionSpec.getAttributes().containsKey(key) && executionSpec
						.getAttributes().get(key).getValue() != null);
	}

	public String toString() {
		return new StringBuffer("Flow ").append(name).toString();
	}

	public boolean equals(Object obj) {
		return ((ExecutionFlow) obj).getName().equals(name);
	}

	public String getPath() {
		return path;
	}
	
	
}
