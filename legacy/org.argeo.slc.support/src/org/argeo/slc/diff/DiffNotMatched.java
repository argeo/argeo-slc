package org.argeo.slc.diff;

import org.argeo.slc.SlcException;

/** Diff issue where reached and expected values are different. */
public class DiffNotMatched extends DiffIssueKey {

	// To enable hibernate persistance, these object cannot be final
	// private final Object expected;
	// private final Object reached;

	private Object expected;
	private Object reached;

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

	@SuppressWarnings("unused")
	private String getExpectedStr() {
		if (expected instanceof String)
			return (String) expected;
		else
			throw new SlcException(
					"Object 'expected' is of wrong type. Must be a String");
	}

	@SuppressWarnings("unused")
	private String getReachedStr() {
		if (reached instanceof String)
			return (String) reached;
		else
			throw new SlcException(
					"Object 'reached' is of wrong type. Must be a String");
	}

	@SuppressWarnings("unused")
	private void setReachedStr(String reachedStr) {
		this.reached = reachedStr;
	}

	@SuppressWarnings("unused")
	private void setExpectedStr(String expectedStr) {
		this.expected = expectedStr;
	}

}
