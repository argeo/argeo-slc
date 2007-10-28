package org.argeo.slc.ant.test;

import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;

public class SlcCloseTestResultTask extends SAwareTask {
	private String result;
	private String report;

	public void executeActions(String mode) {
		if (!mode.equals(StructureRegistry.READ)) {
			TestResult testResult = (TestResult) getContext().getBean(result);
			testResult.close();

			if (report != null) {
				TestReport testReport = (TestReport) getContext().getBean(
						report);
				if (testReport instanceof StructureAware) {
					((StructureAware) testReport).notifyCurrentPath(
							getRegistry(), null);
				}
				testReport.generateTestReport(testResult);
			}
		}
	}

	public void setResult(String bean) {
		this.result = bean;
	}

	public void setReport(String report) {
		this.report = report;
	}

}
