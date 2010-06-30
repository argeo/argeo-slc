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

package org.argeo.slc.core.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;

public class MultiResourceSet implements ResourceSet {
	private List<ResourceSet> resourceSets = new ArrayList<ResourceSet>();

	public Map<String, Resource> listResources() {
		Map<String, Resource> res = new HashMap<String, Resource>();
		for (ResourceSet resourceSet : resourceSets) {
			res.putAll(resourceSet.listResources());
		}
		return res;
	}

	/** Last listed override previous for the same relative paths. */
	public void setResourceSets(List<ResourceSet> resourceSets) {
		this.resourceSets = resourceSets;
	}

	public List<ResourceSet> getResourceSets() {
		return resourceSets;
	}

}
