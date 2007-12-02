package org.argeo.slc.diff;

import java.util.List;
import java.util.Vector;

/** A basic implementation of <code>DiffResult</code>. */
public class SimpleDiffResult implements DiffResult {
	private List<DiffIssue> issues = new Vector<DiffIssue>();

	public List<DiffIssue> getIssues() {
		return issues;
	}

}
