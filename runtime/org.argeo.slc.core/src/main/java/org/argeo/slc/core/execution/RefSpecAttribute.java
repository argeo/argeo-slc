package org.argeo.slc.core.execution;

public class RefSpecAttribute extends AbstractSpecAttribute {
	private Class<?> targetClass;
	/** Read only. */
	private String targetClassName;
	private Object value = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
		this.targetClassName = targetClass.getName();
	}

	public String getTargetClassName() {
		return targetClassName;
	}

}
