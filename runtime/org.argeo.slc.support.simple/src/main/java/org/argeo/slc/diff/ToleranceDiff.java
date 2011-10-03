package org.argeo.slc.diff;

import java.util.Map;

/** A diff which can manage tolerances. */
public interface ToleranceDiff extends Diff {

	/** Get tolerances, key is the column name. */
	public Map<String, String> getTolerances();
}
