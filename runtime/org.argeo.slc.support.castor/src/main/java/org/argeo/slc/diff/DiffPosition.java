package org.argeo.slc.diff;

/** The position of a diff issue within the test resource. */
public abstract class DiffPosition implements Comparable<DiffPosition> {
	protected RelatedFile relatedFile;

	public DiffPosition(RelatedFile relatedFile) {
		super();
		this.relatedFile = relatedFile;
	}

	public RelatedFile getRelatedFile() {
		return relatedFile;
	}

}
