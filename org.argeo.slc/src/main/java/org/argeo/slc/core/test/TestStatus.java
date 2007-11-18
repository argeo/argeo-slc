package org.argeo.slc.core.test;

/**
 * Simple statuses.
 * <p>
 * <ul>
 * <li>{@link #PASSED}: the test succeeded</li>
 * <li>{@link #FAILED}: the test could run, but did not reach the expected
 * result</li>
 * <li>{@link #ERROR}: an error during the test run prevented to get a
 * significant information on the tested system.</li>
 * </ul>
 * </p>
 */
public interface TestStatus {
	/** The flag for a passed test: 0 */
	public final static int PASSED = 0;
	/** The flag for a failed test: 1 */
	public final static int FAILED = 1;
	/**
	 * The flag for a test which could not properly run because of an error
	 * (there is no feedback on the behavior of the tested component): 2
	 */
	public final static int ERROR = 2;
}
