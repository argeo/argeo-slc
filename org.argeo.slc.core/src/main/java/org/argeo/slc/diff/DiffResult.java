package org.argeo.slc.diff;

import java.util.List;

/**
 * The result of a diff. Can be subclassed to provided more structured
 * information.
 */
public interface DiffResult {
	/** The list of issues, a zero size meaning that the diff succeeded. */
	public List<DiffIssue> getIssues();
}
