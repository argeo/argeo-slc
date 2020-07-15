package org.argeo.slc.diff;

/**
 * A value missing in one of the file. If its position is related to expected,
 * this means it is a left over in the reached, if its position is related to
 * the reached it means that it is missing from the reached. If the value is
 * null it means that the entire line is missing.
 */
public class DiffMissing extends DiffIssueKey {

	public DiffMissing(DiffPosition position, DiffKey key) {
		super(position);
		super.key = key;
	}

	@Override
	public String toString() {
		if (position.relatedFile == RelatedFile.EXPECTED) {
			return position + ": left over " + super.toString();
		} else if (position.relatedFile == RelatedFile.REACHED) {
			return position + ": missing " + super.toString();
		}
		return super.toString();
	}

}
