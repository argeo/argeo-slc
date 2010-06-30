/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.core.test.tree;

import java.util.SortedSet;
import java.util.TreeSet;

public class TreeTestResultCollection implements
		Comparable<TreeTestResultCollection> {
	private String id;
	private SortedSet<TreeTestResult> results = new TreeSet<TreeTestResult>();

	public TreeTestResultCollection() {
	}

	public TreeTestResultCollection(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SortedSet<TreeTestResult> getResults() {
		return results;
	}

	public void setResults(SortedSet<TreeTestResult> results) {
		this.results = results;
	}

	public int compareTo(TreeTestResultCollection o) {
		return getId().compareTo(o.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TreeTestResultCollection) {
			return getId().equals(((TreeTestResultCollection) o).getId());
		}
		return false;
	}
}
