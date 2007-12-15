package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.argeo.slc.core.UnsupportedException;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

/** Tree based implementation of a structure registry. */
public class TreeSRegistry implements StructureRegistry {
	/** For ORM */
	private Long tid;
	private TreeSPath root;
	private Map<TreeSPath, SimpleSElement> elements = new TreeMap<TreeSPath, SimpleSElement>();

	private String mode = StructureRegistry.ALL;

	private List<StructurePath> activePaths;

	public StructureElement getElement(StructurePath path) {
		return elements.get(path);
	}

	public List<StructureElement> listElements() {
		return new Vector<StructureElement>(elements.values());
	}

	public List<StructurePath> listPaths() {
		return new Vector<StructurePath>(elements.keySet());
	}

	public void register(StructurePath path, StructureElement element) {
		final SimpleSElement simpleSElement;
		if (element instanceof SimpleSElement) {
			simpleSElement = (SimpleSElement) element;
		} else {
			simpleSElement = new SimpleSElement(element.getLabel());
		}

		if (!(path instanceof TreeSPath))
			throw new UnsupportedException("path", path);

		elements.put((TreeSPath) path, simpleSElement);

	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<StructurePath> getActivePaths() {
		return activePaths;
	}

	public void setActivePaths(List<StructurePath> activePaths) {
		this.activePaths = activePaths;
	}

	/** Gets the related root path. */
	public TreeSPath getRoot() {
		return root;
	}

	/** Sets the related root path. */
	public void setRoot(TreeSPath root) {
		this.root = root;
	}

	/** Gets the elements. */
	public Map<TreeSPath, SimpleSElement> getElements() {
		return elements;
	}

	/** Sets the elements (for ORM). */
	public void setElements(Map<TreeSPath, SimpleSElement> elements) {
		this.elements = elements;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
