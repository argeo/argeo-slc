package org.argeo.slc.diff;

/** Intermediate class that can hold the key to be displayed. */
public abstract class DiffIssueKey extends DiffIssue {
	/** The position of this issue. */
	protected DiffKey key;

	/** Constructor without key*/
	public DiffIssueKey(DiffPosition position) {
		super(position);
	}
	
	/** Constructor with key*/
	public DiffIssueKey(DiffPosition position, DiffKey key) {
		super(position);
		this.key = key;
	}

	public Object getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		if (key != null) {
			return key.toString();
		} else {
			return "";
		}
	}
}
