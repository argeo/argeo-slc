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

import org.argeo.slc.SlcException;

/**
 * A spec attribute wrapping a primitive value.
 * 
 * @see PrimitiveAccessor
 */
public class PrimitiveSpecAttribute extends AbstractSpecAttribute implements
		PrimitiveAccessor {
	private static final long serialVersionUID = -566676381839825483L;
	private String type = "string";
	private Object value = null;

	public PrimitiveSpecAttribute() {
	}

	public PrimitiveSpecAttribute(String type, Object value) {
		this.type = type;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		// check whether type is recognized.
		if (PrimitiveUtils.typeAsClass(type) == null)
			throw new SlcException("Unrecognized type " + type);
		this.type = type;

	}

	@Override
	public String toString() {
		return "Primitive spec attribute [" + type + "]"
				+ (value != null ? "=" + value : "");
	}

}
