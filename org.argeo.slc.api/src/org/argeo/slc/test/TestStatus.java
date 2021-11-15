package org.argeo.slc.test;

/**
 * Simple statuses. Ordering of the flags can be relied upon in aggregation: if
 * one element is failed, the aggregation is failed. Is one element is in ERROR,
 * the aggregation is in ERROR.
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
	public final static Integer PASSED = 0;
	/** The flag for a failed test: 1 */
	public final static Integer FAILED = 1;
	/**
	 * The flag for a test which could not properly run because of an error
	 * (there is no feedback on the behavior of the tested component): 2
	 */
	public final static Integer ERROR = 2;
	public final static String STATUSSTR_PASSED = "PASSED";
	public final static String STATUSSTR_FAILED = "FAILED";
	public final static String STATUSSTR_ERROR = "ERROR";

}
