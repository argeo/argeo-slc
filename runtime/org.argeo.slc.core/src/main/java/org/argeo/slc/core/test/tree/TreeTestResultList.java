package org.argeo.slc.core.test.tree;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.msg.ObjectList;

/**
 * List of results. Used for marshaling.
 * 
 * @deprecated Should be replaced by a standard {@link ObjectList}.
 */
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
