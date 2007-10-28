package org.argeo.slc.core.test;

import java.util.List;

/** The result of a test */
public interface TestResult {
	public TestResultId getTestResultId();
	public List<TestResultPart> listResultParts();
	public void addResultPart(TestResultPart part);
}
