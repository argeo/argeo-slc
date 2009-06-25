package org.argeo.slc.core.execution.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

/** Merge the provided lists in one single list, in the order provided. */
public class MergedLists implements FactoryBean {
	private List<List<Object>> lists = new ArrayList<List<Object>>();

	public void setLists(List<List<Object>> lists) {
		this.lists = lists;
	}

	public Object getObject() throws Exception {
		List<Object> merged = new ArrayList<Object>();
		for (List<Object> lst : lists) {
			merged.addAll(lst);
		}
		return merged;
	}

	public Class<?> getObjectType() {
		return List.class;
	}

	public boolean isSingleton() {
		return false;
	}

}
