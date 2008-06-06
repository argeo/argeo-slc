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
	public final static String STATUS_ACTIVE = "STATUS_ACTIVE";

	/** For ORM */
	private Long tid;
	private String status;
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
		final SimpleSElement simpleSElement;
		if (element instanceof SimpleSElement) {
			simpleSElement = (SimpleSElement) element;
		} else {
			simpleSElement = new SimpleSElement(element.getLabel());
		}

		if (path == null)
			throw new UnsupportedException("Path cannot be null.");

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
