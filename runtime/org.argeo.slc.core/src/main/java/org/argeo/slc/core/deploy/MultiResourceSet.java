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

}
