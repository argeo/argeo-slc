package org.argeo.slc.diff;

/** The root class for issues which happened during a diff. */
public abstract class DiffIssue implements Comparable<DiffIssue> {
	/** The position of this issue. */
	// Was final and is not anymore in order to persist in hibernate
	protected DiffPosition position;

	// hibernate
	private long id;

	/** Constructor */
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

	// Hibernate
	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setPosition(DiffPosition position) {
		this.position = position;
	}

}
