/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

public abstract class AbstractSpecAttribute implements ExecutionSpecAttribute,
		Serializable {
	private static final long serialVersionUID = 6494963738891709440L;
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
