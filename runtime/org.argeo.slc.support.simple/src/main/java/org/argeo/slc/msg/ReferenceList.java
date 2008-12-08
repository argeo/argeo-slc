package org.argeo.slc.msg;

import java.util.List;
import java.util.Vector;

public class ReferenceList {
	private List<String> references = new Vector<String>();

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> refs) {
		this.references = refs;
	}

}
