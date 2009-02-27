package org.argeo.slc.core.execution;

import org.argeo.slc.execution.ExecutionSpecAttribute;

public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute {
	private Boolean isParameter = false;
	private Boolean isFrozen = false;
	private Boolean isHidden = false;

	/** Has to be set at instantiation */
	public Boolean getIsParameter() {
		return isParameter;
	}

	public void setIsParameter(Boolean isParameter) {
		this.isParameter = isParameter;
	}

	/** Cannot be overridden at runtime */
	public Boolean getIsFrozen() {
		return isFrozen;
	}

	public void setIsFrozen(Boolean isFinal) {
		this.isFrozen = isFinal;
	}

	/** Should not be shown to the end user */
	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

}
