package org.argeo.slc.diff;

import org.springframework.core.io.Resource;

/** A comparator providing structured information about the differences found. */
public interface Diff {
	/** Performs the comparison. */
	public DiffResult compare(Resource expected, Resource reached);
}
