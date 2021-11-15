package org.argeo.slc.diff;

/** The position of a diff issue within the test resource. */
public abstract class DiffPosition implements Comparable<DiffPosition> {
	protected RelatedFile relatedFile;

	public DiffPosition(RelatedFile relatedFile) {
		super();
		this.relatedFile = relatedFile;
	}

	// For Hibernate
	DiffPosition() {
	}

	public RelatedFile getRelatedFile() {
		return relatedFile;
	}

	// Added to enable the new data model for persisting TabularDiffTestResult
	@SuppressWarnings("unused")
	private Boolean getIsReached() {
		return relatedFile.equals(RelatedFile.REACHED);
	}

	@SuppressWarnings("unused")
	private void setIsReached(Boolean isReached) {
		this.relatedFile = (isReached ? RelatedFile.REACHED
				: RelatedFile.EXPECTED);
	}

}
