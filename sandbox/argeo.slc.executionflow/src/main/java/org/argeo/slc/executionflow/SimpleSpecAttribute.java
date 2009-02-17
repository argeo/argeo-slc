package org.argeo.slc.executionflow;

public class SimpleSpecAttribute<T> implements ExecutionSpecAttribute<T> {
	 private T value = null;

	public T getValue() {
		return value;
	}
	 
	public void setValue(T value){
		this.value = value;
	}
}
