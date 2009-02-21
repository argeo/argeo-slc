package org.argeo.slc.executionflow;

public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute {
	private Boolean isParameter = true;

	public Boolean getIsParameter() {
		return isParameter;
	}

	public void setIsParameter(Boolean isParameter) {
		this.isParameter = isParameter;
	}

}
