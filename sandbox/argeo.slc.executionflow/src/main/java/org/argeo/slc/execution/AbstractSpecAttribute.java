package org.argeo.slc.execution;

public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute {
	private Boolean isParameter = true;

	public Boolean getIsParameter() {
		return isParameter;
	}

	public void setIsParameter(Boolean isParameter) {
		this.isParameter = isParameter;
	}

}
