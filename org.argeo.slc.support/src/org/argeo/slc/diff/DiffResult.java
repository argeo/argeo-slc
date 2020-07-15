package org.argeo.slc.diff;

/**
 * The result of a diff, to be subclassed in order to provide richer information
 */
public interface DiffResult {
	/** Adds a diff issue */
	public void addDiffIssue(DiffIssue diffIssue);

}
