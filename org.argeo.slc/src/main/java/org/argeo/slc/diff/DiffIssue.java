package org.argeo.slc.diff;

/** The root class for issues which happened during a diff. */
public abstract class DiffIssue implements Comparable<DiffIssue> {
	protected final DiffPosition position;

	public DiffIssue(DiffPosition position) {
		super();
		this.position = position;
	}

	public int compareTo(DiffIssue o) {
		return position.compareTo(o.position);
	}

	/** The position of this issue within the test file */
	public DiffPosition getPosition() {
		return position;
	}
}
