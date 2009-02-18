package org.argeo.slc.executionflow;

public class RefSpecAttribute implements ExecutionSpecAttribute {
	private Class targetClass;
	private Object value = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

}
