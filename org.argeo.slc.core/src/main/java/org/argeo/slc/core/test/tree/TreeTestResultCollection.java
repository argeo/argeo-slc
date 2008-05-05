package org.argeo.slc.core.test.tree;

import java.util.SortedSet;
import java.util.TreeSet;

public class TreeTestResultCollection {
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

}
