package org.argeo.slc.core.structure;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Default implementation of a <code>StructureRegistry</code>.*/
public class DefaultSRegistry implements StructureRegistry {
	private static Log log = LogFactory.getLog(DefaultSRegistry.class);

	private List<StructureElement> elements = new Vector<StructureElement>();
	private List<StructurePath> paths = new Vector<StructurePath>();
	private String mode = StructureRegistry.ALL;

	private List<StructurePath> activePaths;

	public List<StructureElement> listElements() {
		return new Vector<StructureElement>(elements);
	}

	public List<StructurePath> listPaths() {
		return new Vector<StructurePath>(paths);
	}

	public void register(StructurePath path,StructureElement element) {
		StructureElement treeSElement = element;
		elements.add(treeSElement);
		paths.add( path);
		log.debug("Registered " + path + " (desc: '"
				+ treeSElement.getDescription() + "', position: "
				+ elements.size() + ")");
	}

	public void register(StructurePath path,StructureAware structureAware) {
		register(path,structureAware.getElement());
		structureAware.onRegister(this,path);
	}

	public StructureElement getElement(StructurePath path) {
		int index = paths.indexOf(path);
		if (index >= 0) {
			return elements.get(index);
		} else {// not found
			return null;
		}
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

}
