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

package org.argeo.slc.core.test.context;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.argeo.slc.test.context.ContextAware;
import org.argeo.slc.test.context.ParentContextAware;
import org.springframework.beans.factory.InitializingBean;

public class SimpleParentContextAware extends SimpleContextAware implements
		ParentContextAware, InitializingBean {
	private List<ContextAware> children = new Vector<ContextAware>();

	public Collection<ContextAware> getChildContexts() {
		return children;
	}

	public void addChildContext(ContextAware contextAware) {
		children.add(contextAware);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (getParentContext() != null) {
			// If has a parent, sync it.
			super.afterPropertiesSet();
		} else {
			if (children.size() > 0) {
				// No need to synchronize if no children
				ContextUtils.synchronize(this);
			}
		}
	}
}
