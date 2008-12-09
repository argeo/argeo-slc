package org.argeo.slc.core.test.tree;

import java.util.ArrayList;
import java.util.List;

/** List of results. Used for marshaling. */
public class TreeTestResultList {
	private List<TreeTestResult> list = new ArrayList<TreeTestResult>();

	public TreeTestResultList() {
		super();
	}

	public TreeTestResultList(List<TreeTestResult> list) {
		super();
		this.list = list;
	}

	public List<TreeTestResult> getList() {
		return list;
	}

	public void setList(List<TreeTestResult> list) {
		this.list = list;
	}

}
