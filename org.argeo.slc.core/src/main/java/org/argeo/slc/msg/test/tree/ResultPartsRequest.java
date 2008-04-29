package org.argeo.slc.msg.test.tree;

import java.util.Map;
import java.util.TreeMap;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.PartSubList;

public class ResultPartsRequest {
	private String resultUuid;
	private Map<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
	private Map<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();

	public String getResultUuid() {
		return resultUuid;
	}

	public void setResultUuid(String resultUuid) {
		this.resultUuid = resultUuid;
	}

	public Map<TreeSPath, PartSubList> getResultParts() {
		return resultParts;
	}

	public void setResultParts(Map<TreeSPath, PartSubList> resultParts) {
		this.resultParts = resultParts;
	}

	public Map<TreeSPath, StructureElement> getElements() {
		return elements;
	}

	public void setElements(Map<TreeSPath, StructureElement> elements) {
		this.elements = elements;
	}
}
