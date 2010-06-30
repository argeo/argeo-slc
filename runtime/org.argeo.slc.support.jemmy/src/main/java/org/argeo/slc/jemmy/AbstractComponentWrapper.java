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

package org.argeo.slc.jemmy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netbeans.jemmy.operators.ComponentOperator;

public class AbstractComponentWrapper implements ComponentWrapper {

	protected ComponentWrapper parent;

	/**
	 * List of children ComponentWrapper
	 */
	protected List children = new ArrayList();

	protected WrapperLocator locator;

	protected String prefix;

	public ComponentOperator find() {
		return locator.find(this);
	}

	protected String createNewKey(String oldKey) {
		return (prefix == null) ? oldKey : (prefix + "." + oldKey);
	}

	protected void addToAccessorMap(Map accessors, String oldKey,
			Object accessor) {
		String newKey = createNewKey(oldKey);
		if (accessors.containsKey(newKey)) {
			throw new ConfigRuntimeException("An Accessor with key '" + newKey
					+ "' was already registered");
		}
		accessors.put(newKey, accessor);
	}

	public Map getAccessors(Class accessorClass) {
		Map accessors = new HashMap();
		if (accessorClass.isInstance(this)) {
			addToAccessorMap(accessors, ((Accessor) this).getFieldName(), this);
		}
		for (int i = 0; i < children.size(); i++) {
			Map childAccessors = ((ComponentWrapper) children.get(i))
					.getAccessors(accessorClass);

			Set entries = childAccessors.entrySet();
			Iterator it = entries.iterator();

			while (it.hasNext()) {
				Map.Entry keyValue = (Map.Entry) it.next();
				addToAccessorMap(accessors, keyValue.getKey().toString(),
						keyValue.getValue());
			}
		}
		return accessors;
	}

	public ComponentWrapper getParent() {
		return parent;
	}

	public void setParent(ComponentWrapper parent) {
		if (this.parent != null) {
			throw new ConfigRuntimeException("Parent already set");
		}
		this.parent = parent;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;

		// check that all elements of the list are ComponentWrapper
		// and set their parent
		for (int i = 0; i < this.children.size(); i++) {
			ComponentWrapper wrapper = (ComponentWrapper) this.children.get(i);
			if (wrapper == null) {
				throw new ConfigRuntimeException(
						"Children of ComponentWrappers must be ComponentWrappers");
			}
			wrapper.setParent(this);
		}
	}

	public WrapperLocator getLocator() {
		return locator;
	}

	public void setLocator(WrapperLocator locator) {
		this.locator = locator;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
