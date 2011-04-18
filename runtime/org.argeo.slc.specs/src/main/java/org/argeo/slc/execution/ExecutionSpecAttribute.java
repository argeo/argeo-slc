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

package org.argeo.slc.execution;

/**
 * This interface stands for one attribute of a given flow.
 * 
 * We mainly have two implementations :
 * 
 * + Primitive attributes (no predefined choice, the end user must compute a
 * String, a Float, an Integer...)
 * 
 * + RefSpecAttribute which enable two things + a reference to another object of
 * the application context + the display of some choices among which the end
 * user can choose.
 * 
 * @see org.argeo.slc.core.execution.PrimitiveSpecAttribute
 * @see org.argeo.slc.core.execution.RefSpecAttribute
 * @see org.argeo.slc.core.execution.PrimitiveUtils : this class offers some
 *      helper, among others to cast the various type of primitive attribute.
 */
public interface ExecutionSpecAttribute {
	public Object getValue();

	public Boolean getIsParameter();

	public Boolean getIsFrozen();

	public Boolean getIsHidden();

}
