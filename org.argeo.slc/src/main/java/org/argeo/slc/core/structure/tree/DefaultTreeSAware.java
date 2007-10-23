package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

public class DefaultTreeSAware implements TreeSAware{
	private TreeSElement element;
	private List<TreeSAware> children = new Vector<TreeSAware>();

	public StructureElement getElement() {
		return element;
	}

	public void setElement(TreeSElement element) {
		this.element = element;
	}

	public void onRegister(StructureRegistry registry) {
		for(TreeSAware sAware : children){
			registry.register(sAware.getElement());
			sAware.onRegister(registry);
		}
	}

	public void addChild(TreeSAware sAware){
		children.add(sAware);
	}

	public List<TreeSAware> getChildren() {
		return children;
	}
	
}
