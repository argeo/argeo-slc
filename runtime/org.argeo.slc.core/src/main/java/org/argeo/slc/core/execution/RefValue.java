package org.argeo.slc.core.execution;

public class RefValue extends AbstractExecutionValue {
	private String label;

	public RefValue() {
	}

	public RefValue(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
