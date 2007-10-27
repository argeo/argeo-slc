package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.StructureElement;

/**
 * Implementation of <code>StructureElement</code> for tree based registries,
 * using <code>TreeSPath</code>
 * 
 * @see TreeSPath
 */
public class TreeSElement implements StructureElement {
	private String description;

	public TreeSElement(String description){
		this.description = description;
	}
	
	public TreeSElement(String description, String defaultDescription){
		this(description!=null?description:defaultDescription);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
