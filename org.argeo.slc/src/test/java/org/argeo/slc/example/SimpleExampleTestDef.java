package org.argeo.slc.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestRun;
import org.argeo.slc.example.appli.ExampleAppli;

public class SimpleExampleTestDef implements TestDefinition {
	private Log log = LogFactory.getLog(SimpleExampleTestDef.class);

	public void execute(TestRun testRun) {
		ExampleDeployedSystem system = (ExampleDeployedSystem) testRun
				.getDeployedSystem();

		ExampleTestData data = (ExampleTestData) testRun.getTestData();

		ExampleAppli appli = system.getExampleAppliInstance();
		String[] args = { data.getInputFile().getAbsolutePath(),
				data.getReachedFile().getAbsolutePath() };

		TestResult result = testRun.getTestResult();

		SimpleResultPart executePart = new SimpleResultPart();
		executePart.setMessage("Execute example appli");
		try {
			// execute
			appli.filter(args);

			executePart.setStatus(TestStatus.PASSED);
		} catch (Exception e) {
			executePart.setStatus(TestStatus.ERROR);
			executePart.setException(e);
		}
		result.addResultPart(executePart);
		if (executePart.getStatus() == TestStatus.ERROR) {
			return;
		}

		result.addResultPart(assertFiles(data));
	}

	private TestResultPart assertFiles(ExampleTestData data) {
		SimpleResultPart part = new SimpleResultPart();
		part.setMessage("Compare " + data.getExpectedFile().getName()
				+ " with " + data.getReachedFile().getName());
		try {
			String expected = loadFile(data.getExpectedFile());
			String reached = loadFile(data.getReachedFile());
			part.setStatus(expected.equals(reached) ? TestStatus.PASSED
					: TestStatus.FAILED);
		} catch (Exception e) {
			part.setStatus(TestStatus.ERROR);
			part.setException(e);
			log.error("Error while asserting files", e);
		}
		return part;
	}

	public String loadFile(File file) throws IOException {
		StringBuffer buf = new StringBuffer("");
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			buf.append(line).append('\n');
		}
		in.close();
		return buf.toString();
	}
}
