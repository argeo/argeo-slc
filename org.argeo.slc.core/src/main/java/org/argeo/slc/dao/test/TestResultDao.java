package org.argeo.slc.dao.test;

import java.util.List;

import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;

/**
 * The dao for <code>TestResult</code>.
 * 
 * @see TestResult
 */
public interface TestResultDao<T extends TestResult> {
	/** Gets a test result based on its id. */
	public T getTestResult(TestResultId id);

	/** Persists a new test result. */
	public void create(TestResult testResult);

	/** Updates an already persisted test result. */
	public void update(TestResult testResult);

	/** Lists all test results. */
	public List<T> listTestResults();
}
