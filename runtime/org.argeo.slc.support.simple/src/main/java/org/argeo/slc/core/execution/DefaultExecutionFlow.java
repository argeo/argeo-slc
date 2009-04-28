package org.argeo.slc.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureRegistry;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.MapBindingResult;

public class DefaultExecutionFlow implements ExecutionFlow, InitializingBean,
		BeanNameAware, StructureAware<TreeSPath>, ResourceLoaderAware {

	private final static Log log = LogFactory
			.getLog(DefaultExecutionFlow.class);

	private final ExecutionSpec executionSpec;
	private String name = null;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Runnable> executables = new ArrayList<Runnable>();

	private String path;
	private StructureRegistry<TreeSPath> registry = new TreeSRegistry();

	private ResourceLoader resourceLoader = null;

	public DefaultExecutionFlow() {
		this.executionSpec = new DefaultExecutionSpec();
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public DefaultExecutionFlow(ExecutionSpec executionSpec,
			Map<String, Object> parameters) {
		// be sure to have an execution spec
		this.executionSpec = (executionSpec == null) ? new DefaultExecutionSpec()
				: executionSpec;

		// only parameters contained in the executionSpec can be set
		for (String parameter : parameters.keySet()) {
			if (!executionSpec.getAttributes().containsKey(parameter)) {
				throw new SlcException("Parameter " + parameter
						+ " is not defined in the ExecutionSpec");
			}
		}

		// set the parameters
		this.parameters.putAll(parameters);

		// check that all the required parameters are defined
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
		}

		if (errors.hasErrors())
			throw new SlcException("Could not prepare execution flow: "
					+ errors.toString());

	}

	public void run() {
		for (Runnable executable : executables) {
			executable.run();
		}
	}

	public void afterPropertiesSet() throws Exception {
		if (path != null) {
			for (Runnable executable : executables) {
				if (executable instanceof StructureAware) {
					((StructureAware<TreeSPath>) executable).notifyCurrentPath(
							registry, new TreeSPath(path));
				}
			}
		}
	}

	public void setBeanName(String name) {
		this.name = name;
	}

	public void setExecutables(List<Runnable> executables) {
		this.executables = executables;
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

	public Object getParameter(String parameterName) {
		// Verify that there is a spec attribute
		ExecutionSpecAttribute specAttr = null;
		if (executionSpec.getAttributes().containsKey(parameterName)) {
			specAttr = executionSpec.getAttributes().get(parameterName);
		} else {
			throw new SlcException("Key " + parameterName
					+ " is not defined in the specifications of " + toString());
		}

		if (parameters.containsKey(parameterName)) {
			Object paramValue = parameters.get(parameterName);
			if (specAttr instanceof ResourceSpecAttribute) {
				// deal with resources
				Resource resource = resourceLoader.getResource(paramValue
						.toString());
				return ((ResourceSpecAttribute) specAttr)
						.convertResource(resource);
			} else {
				return paramValue;
			}
		} else {
			if (specAttr.getValue() != null) {
				return specAttr.getValue();
			}
		}
		throw new SlcException("Key " + parameterName
				+ " is not set as parameter in " + toString());
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

	public void setPath(String path) {
		this.path = path;
	}

	public void setRegistry(StructureRegistry<TreeSPath> registry) {
		this.registry = registry;
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		if (this.path == null) {
			this.path = path.toString();
		}
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
