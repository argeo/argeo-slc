package org.argeo.slc.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.RefSpecAttribute;
import org.argeo.slc.execution.RefValueChoice;

/** Spring based implementation of execution specifications. */
public class DefaultExecutionSpec implements ExecutionSpec, Serializable {
	private static final long serialVersionUID = 7042162759380893595L;
	private String description;
	private Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

	private String name = INTERNAL_NAME;

	public Map<String, ExecutionSpecAttribute> getAttributes() {
		return attributes;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAttributes(Map<String, ExecutionSpecAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The Spring bean name (only relevant for specs declared has high-level beans)
	 */
	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		return ((ExecutionSpec) obj).getName().equals(name);
	}

	/**
	 * The Spring bean description (only relevant for specs declared has high-level
	 * beans)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Generates a list of ref value choices based on the bean available in the
	 * application ocntext.
	 */
	protected List<RefValueChoice> buildRefValueChoices(RefSpecAttribute rsa) {
		List<RefValueChoice> choices = new ArrayList<RefValueChoice>();
		// FIXME implement something
		return choices;
	}

}
