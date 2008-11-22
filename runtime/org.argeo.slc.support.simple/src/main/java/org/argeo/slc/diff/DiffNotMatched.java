package org.argeo.slc.diff;

/** Diff issue where reached and expected values are different. */
public class DiffNotMatched extends DiffIssueKey {
	private final Object expected;
	private final Object reached;

	public DiffNotMatched(DiffPosition position, Object expected, Object reached) {
		super(position);
		this.expected = expected;
		this.reached = reached;
	}

	public DiffNotMatched(DiffPosition position, Object expected,
			Object reached, DiffKey key) {
		super(position, key);
		this.expected = expected;
		this.reached = reached;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getReached() {
		return reached;
	}

	@Override
	public String toString() {
		String result = position + ": not matched " + expected + " <> "
				+ reached;
		if (super.key != null) {
			result = result + " - Key: " + super.toString();
		}

		return result;
	}

}
