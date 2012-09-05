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
package org.argeo.slc.castor.structure.tree;

import org.exolab.castor.mapping.AbstractFieldHandler;
import org.exolab.castor.mapping.MapItem;

import org.argeo.slc.core.structure.tree.TreeSPath;

public class TreeSPathFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		MapItem part = (MapItem) object;
		return ((TreeSPath) part.getKey()).getAsUniqueString();
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		return null;
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
		MapItem part = (MapItem) object;
		part.setKey(null);
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		MapItem part = (MapItem) object;
		part.setKey(new TreeSPath(value.toString()));
	}

}
