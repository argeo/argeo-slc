package org.argeo.slc.core.execution;

public class PrimitiveValue extends AbstractExecutionValue implements
		PrimitiveAccessor {
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
