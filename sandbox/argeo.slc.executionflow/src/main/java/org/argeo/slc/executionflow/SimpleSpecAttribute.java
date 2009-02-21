package org.argeo.slc.executionflow;

public class SimpleSpecAttribute extends AbstractSpecAttribute {
	private Object value = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
