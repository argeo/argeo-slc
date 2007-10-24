package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

/**
 * Default implementation of <code>TreeSAware</code> for tree based
 * registries, using <code>TreeSPath</code>. Convenient to be wrapped in
 * classes which cannot extend it.
 */
public class DefaultTreeSAware implements TreeSAware {
	private StructureElement element;
	private List<StructureAware> children = new Vector<StructureAware>();

	public StructureElement getElement() {
		return element;
	}

	public void setElement(StructureElement element) {
		this.element = element;
	}

	public void onRegister(StructureRegistry registry) {
		for (StructureAware sAware : children) {
			registry.register(sAware.getElement());
			sAware.onRegister(registry);
		}
	}

	public void addToPropagationList(StructureAware sAware) {
		children.add(sAware);
	}

	public List<StructureAware> getPropagationList() {
		return children;
	}

}
