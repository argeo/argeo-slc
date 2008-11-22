package org.argeo.slc.dao.test;

import java.util.Date;
import java.util.List;

import org.argeo.slc.test.TestResult;

/**
 * The dao for <code>TestResult</code>.
 * 
 * @see TestResult
 */
public interface TestResultDao<T extends TestResult> {
	/** Gets a test result based on its id. */
	public T getTestResult(String uuid);

	/** Persists a new test result. */
	public void create(TestResult testResult);

	/** Updates an already persisted test result. */
	public void update(TestResult testResult);

	/** Lists all test results. */
	public List<T> listTestResults();

	public void close(String id, Date closeDate);
}
