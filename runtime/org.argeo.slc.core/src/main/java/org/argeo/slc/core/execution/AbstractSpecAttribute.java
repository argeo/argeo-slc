/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.core.execution;

import java.io.Serializable;

import org.argeo.slc.execution.ExecutionSpecAttribute;

/** Canonical implementation of the execution spec attribute booleans. */
public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute,
		Serializable {
	private static final long serialVersionUID = 6494963738891709440L;
	private Boolean isImmutable = false;
	private Boolean isConstant = false;
	private Boolean isHidden = false;

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

}
