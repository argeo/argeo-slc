package org.argeo.slc.msg;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList {
	private List<String> references = new ArrayList<String>();

	public ReferenceList() {
	}

	public ReferenceList(List<String> references) {
		this.references = references;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> refs) {
		this.references = refs;
	}

}
