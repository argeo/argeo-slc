/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
