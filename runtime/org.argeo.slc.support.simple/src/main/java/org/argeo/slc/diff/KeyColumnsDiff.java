package org.argeo.slc.diff;

import java.util.List;

/**
 * Diff which is based on comparison of multiple-key atomic elements (typically
 * columns in a tabular content)
 */
public interface KeyColumnsDiff extends Diff {
	/** $The list of key columns. */
	List<String> getKeyColumns();
}
