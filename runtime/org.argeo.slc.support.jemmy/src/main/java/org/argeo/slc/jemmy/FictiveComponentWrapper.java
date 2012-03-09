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
package org.argeo.slc.jemmy;

import java.util.HashMap;
import java.util.Map;

import org.netbeans.jemmy.operators.ComponentOperator;

public class FictiveComponentWrapper implements ComponentWrapper {

	protected ComponentWrapper parent;	
	
	public ComponentOperator find() {
		// just ask the parent
		return parent.find();
	}

	/**
	 * Return only itself (if the class matches)
	 */
	public Map getAccessors(Class accessorClass) {
		Map accessors = new HashMap();
		if (accessorClass.isInstance(this)) {
			accessors.put(((Accessor) this).getFieldName(), this);
		}		
		return accessors;
	}

	public ComponentWrapper getParent() {
		return parent;
	}

	public void setParent(ComponentWrapper parent) {
		this.parent = parent;
	}

}
