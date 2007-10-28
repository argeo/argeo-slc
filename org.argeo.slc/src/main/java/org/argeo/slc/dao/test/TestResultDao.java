package org.argeo.slc.dao.test;

import java.util.List;

import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;

public interface TestResultDao {
	public TestResult getTestResult(TestResultId id);
	public void create(TestResult testResult);
	public void update(TestResult testResult);
	public List<TestResult> listTestResults();
}
