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

package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureRegistry;

/**
 * Provides default implementations of some methods of <code>TreeSRelated</code>
 * .
 */
public abstract class TreeSRelatedHelper implements TreeSRelated {
	private TreeSPath basePath;
	private StructureRegistry<TreeSPath> registry;

	// private ThreadLocal<TreeSPath> basePath = new ThreadLocal<TreeSPath>();
	// private ThreadLocal<StructureRegistry<TreeSPath>> registry = new
	// ThreadLocal<StructureRegistry<TreeSPath>>();

	public TreeSPath getBasePath() {
		return basePath;
	}

	public StructureRegistry<TreeSPath> getRegistry() {
		return registry;
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		this.basePath = path;
		this.registry = registry;
	}

	public StructureElement getStructureElement(String key) {
		return new SimpleSElement(key);
	}

	/**
	 * Checks wether the object is {@link StructureAware} and forward path and
	 * registry. null safe for both arguments.
	 */
	@SuppressWarnings(value = { "unchecked" })
	protected void forwardPath(Object obj, String childName) {
		if (obj == null)
			return;

		if (obj instanceof StructureAware && basePath != null) {
			TreeSPath path;
			if (childName != null)
				path = basePath.createChild(childName);
			else
				path = basePath;

			((StructureAware<TreeSPath>) obj).notifyCurrentPath(registry, path);
		}
	}

}
