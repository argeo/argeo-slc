package org.argeo.slc.executionflow;

public class RefSpecAttribute<T> implements ExecutionSpecAttribute<T> {
	private Class targetClass;
	private T value = null;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

}
