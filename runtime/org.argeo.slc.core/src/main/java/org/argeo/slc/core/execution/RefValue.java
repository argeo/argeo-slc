package org.argeo.slc.core.execution;


public class RefValue extends AbstractExecutionValue {
	private String ref;

	public RefValue() {
	}

	public RefValue(String ref) {
		super();
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}
