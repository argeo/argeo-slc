package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

public class TreeSRegistry implements StructureRegistry {
	private static Log log = LogFactory.getLog(TreeSRegistry.class);

	private List<TreeSElement> elements = new Vector<TreeSElement>();
	private List<TreeSPath> paths = new Vector<TreeSPath>();

	public List<StructureElement> listElements() {
		return new Vector<StructureElement>(elements);
	}

	public void register(StructureElement element) {
		TreeSElement treeSElement = checkElement(element);
		elements.add(treeSElement);
		paths.add((TreeSPath) treeSElement.getPath());
		log.debug("Registered " + treeSElement.getPath() + " (desc: "
				+ treeSElement.getDescription() + " position: "
				+ elements.size() + ")");
	}

	public void register(StructureAware structureAware) {
		register(structureAware.getElement());
		structureAware.onRegister(this);
	}

	public StructureElement getElement(StructurePath path) {
		int index = paths.indexOf(path);
		if (index >= 0) {
			return elements.get(index);
		} else {// not found
			return null;
		}
	}

	protected TreeSElement checkElement(StructureElement element) {
		if (!(element instanceof TreeSElement)) {
			throw new RuntimeException("Element class " + element.getClass()
					+ " is not supported.");
		}
		return (TreeSElement) element;
	}
}
