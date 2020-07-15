package org.argeo.slc.primitive;

import org.argeo.slc.execution.AbstractExecutionValue;

/** Primitive value to be used by an execution. */
public class PrimitiveValue extends AbstractExecutionValue implements
		PrimitiveAccessor {
	private static final long serialVersionUID = 533414290998374166L;

	private String type;

	private Object value;

	public PrimitiveValue() {
	}

	public PrimitiveValue(String type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
