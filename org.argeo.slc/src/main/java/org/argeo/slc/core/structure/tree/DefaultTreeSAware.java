package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

public class DefaultTreeSAware implements StructureAware{
	private TreeSElement element;
	private List<StructureAware> children = new Vector<StructureAware>();

	public StructureElement getElement() {
		return element;
	}

	public void setElement(TreeSElement element) {
		this.element = element;
	}

	public void onRegister(StructureRegistry registry) {
		for(StructureAware sAware : children){
			registry.register(sAware.getElement());
			sAware.onRegister(registry);
		}
	}

	public void addChild(StructureAware sAware){
		children.add(sAware);
	}
}
