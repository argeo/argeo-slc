package org.argeo.slc.ant.test;

import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;

public class SlcCloseTestResultTask extends AbstractSpringTask {
	private String result;
	private String report;
	
	public void execute(){
		TestResult testResult = (TestResult)getContext().getBean(result);
		testResult.close();
		
		if(report!=null){
			TestReport testReport = (TestReport)getContext().getBean(report);
			testReport.generateTestReport(testResult);
		}
	}

	public void setResult(String bean) {
		this.result = bean;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
	
}
