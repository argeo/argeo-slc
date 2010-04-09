package org.argeo.slc.structure;

import java.util.Map;

/**
 * Atomic element holding metadata such as description about the element which
 * registered.
 */
public interface StructureElement {
	/** Label of this element. */
	public String getLabel();
	
	/** tags attached to this element*/
	public Map<String, String> getTags();

	
}
