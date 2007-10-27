package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

/**
 * Default implementation of <code>TreeSAware</code> for tree based
 * registries, using <code>TreeSPath</code>. Convenient to be wrapped in
 * classes which cannot extend it.
 */
public class DefaultTreeSAware implements StructureAware {
	private StructureElement element;
	private List<String> names = new Vector<String>();
	private List<StructureAware> children = new Vector<StructureAware>();

	public StructureElement getElement() {
		return element;
	}

	public void setElement(StructureElement element) {
		this.element = element;
	}

	public void onRegister(StructureRegistry registry, StructurePath path) {
		int index = 0;
		for (StructureAware sAware : children) {
			TreeSPath childPath = ((TreeSPath) path).createChild(names
					.get(index)
					+ index);
			registry.register(childPath, sAware.getElement());
			sAware.onRegister(registry, childPath);
			index++;
		}
	}

	public void addToPropagationList(String name, StructureAware sAware) {
		names.add(name);
		children.add(sAware);
	}

	public List<StructureAware> getPropagationList() {
		return children;
	}

}
