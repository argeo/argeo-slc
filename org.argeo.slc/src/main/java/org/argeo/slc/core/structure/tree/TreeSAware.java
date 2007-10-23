package org.argeo.slc.core.structure.tree;

import java.util.List;

import org.argeo.slc.core.structure.StructureAware;

public interface TreeSAware extends StructureAware{
	public void setElement(TreeSElement element);

	public void addChild(TreeSAware sAware);
	public List<TreeSAware> getChildren();
}
