package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;

public class TreeSElement implements StructureElement {
	private String description;
	private TreeSPath path;

	private List<TreeSElement> children = new Vector<TreeSElement>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StructurePath getPath() {
		return path;
	}

	public List<TreeSElement> getChildren() {
		return children;
	}

	public TreeSElement createChild(String name, String description) {
		TreeSElement element = new TreeSElement();
		element.path = TreeSPath.createChild((TreeSPath) this.getPath(), name);
		element.description = description;
		children.add(element);
		return element;
	}

	public static TreeSElement createRootElelment(String name,
			String description) {
		TreeSElement element = new TreeSElement();
		element.path = TreeSPath.createChild(null, name);
		element.description = description;
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StructureElement) {
			StructureElement element = (StructureElement) obj;
			return getPath().equals(element.getPath());
		}
		return false;
	}

}
