/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.execution;

/**
 * Possible attribute of an execution flow.
 * 
 * There are mainly two implementations :<br>
 * + Primitive attributes (no predefined choice, the end user must compute a
 * String, a Float, an Integer...)<br>
 * + RefSpecAttribute which enable two things<br>
 * ++ a reference to another object of the application context<br>
 * ++ the display of some choices among which the end user can choose.<br>
 * 
 * @see org.argeo.slc.core.execution.PrimitiveSpecAttribute
 * @see org.argeo.slc.core.execution.RefSpecAttribute
 * @see org.argeo.slc.core.execution.PrimitiveUtils : this class offers some
 *      helper, among others to cast the various type of primitive attribute.
 */
public interface ExecutionSpecAttribute {
	/**
	 * Whether this attribute has to be set at instantiation of the flow and
	 * cannot be modified afterwards. If the attribute is not immutable (that
	 * is, this method returns false), it can be set at execution time.
	 */
	public Boolean getIsImmutable();

	/**
	 * Whether this attribute must be explicitly set and cannot be modified.
	 * This attribute is then basically a constant within a given application
	 * context. {@link #getValue()} cannot return null if the attribute is a
	 * constant.
	 */
	public Boolean getIsConstant();

	/** Whether this attribute will be hidden to end users. */
	public Boolean getIsHidden();

	/**
	 * The default value for this attribute. Can be null, except if
	 * {@link #getIsFrozen()} is <code>true</code>, in which case it represents
	 * the constant value of this attribute.
	 */
	public Object getValue();

	/** Description of this attribute, can be null */
	public String getDescription();

	/** @deprecated use {@link #getIsImmutable()} instead */
	public Boolean getIsParameter();

	/** @deprecated use {@link #getIsConstant()} instead */
	public Boolean getIsFrozen();

}
