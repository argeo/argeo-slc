package org.argeo.slc.core.structure;

import org.argeo.slc.core.structure.tree.TreeSPath;

/**
 * Implementation of <code>StructureElement</code> for tree based registries,
 * using <code>TreeSPath</code>
 * 
 * @see TreeSPath
 */
public class SimpleSElement implements StructureElement {
	private String description;

	public SimpleSElement(String description){
		this.description = description;
	}
	
	public SimpleSElement(String description, String defaultDescription){
		this(description!=null?description:defaultDescription);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
