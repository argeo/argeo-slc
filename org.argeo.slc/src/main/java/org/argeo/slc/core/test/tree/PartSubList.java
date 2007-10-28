package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.test.TestResultPart;

public class PartSubList {
	
	/** For ORM */
	private Long tid;

	private List<TestResultPart> parts = new Vector<TestResultPart>();

	public List<TestResultPart> getParts() {
		return parts;
	}

	public void setParts(List<TestResultPart> parts) {
		this.parts = parts;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	
}
