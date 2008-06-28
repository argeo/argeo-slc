package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.argeo.slc.core.UnsupportedException;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

/** Tree based implementation of a structure registry. */
public class TreeSRegistry implements StructureRegistry<TreeSPath> {
	/** For ORM */
	private Long tid;
	private Map<TreeSPath, SimpleSElement> elements = new TreeMap<TreeSPath, SimpleSElement>();

	private String mode = StructureRegistry.ALL;

	private List<TreeSPath> activePaths;

	public <T extends StructureElement> T getElement(TreeSPath path) {
		return (T) elements.get(path);
	}

	public List<StructureElement> listElements() {
		return new Vector<StructureElement>(elements.values());
	}

	public List<TreeSPath> listPaths() {
		return new Vector<TreeSPath>(elements.keySet());
	}

	public void register(TreeSPath path, StructureElement element) {
		if (path == null)
			throw new UnsupportedException("Cannot register under a null path.");
		if (element == null)
			throw new UnsupportedException(
					"Cannot register null element for path " + path);
		if (element.getLabel() == null)
			throw new UnsupportedException(
					"Cannot register an element with null label for path "
							+ path);

		final SimpleSElement simpleSElement;
		if (element instanceof SimpleSElement) {
			simpleSElement = (SimpleSElement) element;
		} else {
			simpleSElement = new SimpleSElement(element.getLabel());
		}

		elements.put(path, simpleSElement);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<TreeSPath> getActivePaths() {
		return activePaths;
	}

	public void setActivePaths(List<TreeSPath> activePaths) {
		this.activePaths = activePaths;
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
