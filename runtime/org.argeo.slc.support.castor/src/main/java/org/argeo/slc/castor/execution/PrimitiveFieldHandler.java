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
package org.argeo.slc.castor.execution;

import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class PrimitiveFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		if (object == null)
			return null;

		Object value = ((PrimitiveAccessor) object).getValue();
		return value != null ? value.toString() : null;
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		return null;
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		PrimitiveAccessor primitiveAccessor = (PrimitiveAccessor) object;
		String type = primitiveAccessor.getType();
		String str = value.toString();
		primitiveAccessor.setValue(PrimitiveUtils.convert(type, str));
	}

}
