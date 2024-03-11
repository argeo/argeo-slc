package org.argeo.api.slc.execution;

import java.io.Serializable;

/** Canonical implementation of the execution spec attribute booleans. */
public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute,
		Serializable {
	private static final long serialVersionUID = 6494963738891709440L;
	private Boolean isImmutable = false;
	private Boolean isConstant = false;
	private Boolean isHidden = false;

	private String description;

	/** Has to be set at instantiation */
	public Boolean getIsImmutable() {
		return isImmutable;
	}

	public void setIsImmutable(Boolean isImmutable) {
		this.isImmutable = isImmutable;
	}

	/** Cannot be overridden at runtime */
	public Boolean getIsConstant() {
		return isConstant;
	}

	public void setIsConstant(Boolean isConstant) {
		this.isConstant = isConstant;
	}

	/** Should not be shown to the end user */
	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	/*
	 * DEPRECATED
	 */
	/** @deprecated use {@link #getIsImmutable()} instead */
	public Boolean getIsParameter() {
		return isImmutable;
	}

	/** @deprecated use {@link #getIsConstant()} instead */
	public Boolean getIsFrozen() {
		return isConstant;
	}

	/** @deprecated use {@link #setIsImmutable(Boolean)} instead */
	public void setIsParameter(Boolean isParameter) {
		this.isImmutable = isParameter;
	}

	/** @deprecated use {@link #setIsConstant(Boolean)} instead */
	public void setIsFrozen(Boolean isFrozen) {
		this.isConstant = isFrozen;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
