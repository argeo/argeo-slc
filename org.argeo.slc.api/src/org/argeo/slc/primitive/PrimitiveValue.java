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
package org.argeo.slc.primitive;

import org.argeo.slc.execution.AbstractExecutionValue;

/** Primitive value to be used by an execution. */
public class PrimitiveValue extends AbstractExecutionValue implements
		PrimitiveAccessor {
	private static final long serialVersionUID = 533414290998374166L;

	private String type;

	private Object value;

	public PrimitiveValue() {
	}

	public PrimitiveValue(String type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}