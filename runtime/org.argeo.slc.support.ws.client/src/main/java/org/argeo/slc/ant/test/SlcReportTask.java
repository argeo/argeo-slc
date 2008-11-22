package org.argeo.slc.ant.test;

import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;

/** Ant tasks generating a report. */
public class SlcReportTask extends SAwareTask {
	private String result;
	private String report;

	@Override
	public void executeActions(String mode) {
		if (!mode.equals(StructureRegistry.READ)) {
			TestResult testResult = null;
			if (result != null) {
				testResult = (TestResult) getContext().getBean(result);
			}
			TestReport testReport = (TestReport) getContext().getBean(report);
			if (testReport instanceof StructureAware) {
				((StructureAware) testReport).notifyCurrentPath(getRegistry(),
						null);
			}
			testReport.generateTestReport(testResult);
		}
	}

	/** Sets the bean name of the result to close. */
	public void setResult(String bean) {
		this.result = bean;
	}

	/** Sets the bean name of the report to generate. */
	public void setReport(String report) {
		this.report = report;
	}

}
