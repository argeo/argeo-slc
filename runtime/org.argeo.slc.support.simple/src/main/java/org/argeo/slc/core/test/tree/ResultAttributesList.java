package org.argeo.slc.core.test.tree;

import java.util.ArrayList;
import java.util.List;

public class ResultAttributesList {
	private List<ResultAttributes> list = new ArrayList<ResultAttributes>();

	public ResultAttributesList() {
		super();
	}

	public ResultAttributesList(List<ResultAttributes> list) {
		super();
		this.list = list;
	}

	public List<ResultAttributes> getList() {
		return list;
	}

	public void setList(List<ResultAttributes> list) {
		this.list = list;
	}

}
