package org.argeo.slc.execution;

public class RefSpecAttribute  extends AbstractSpecAttribute {
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
