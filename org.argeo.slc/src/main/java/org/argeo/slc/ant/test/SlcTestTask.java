package org.argeo.slc.ant.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.SlcAntConfig;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.WritableTestRun;

/** Ant task wrapping a test run. */
public class SlcTestTask extends SAwareTask {
	Log log = LogFactory.getLog(SlcTestTask.class);

	private TestDefinitionArg testDefinitionArg;
	private TestDataArg testDataArg;
	private DeployedSystemArg deployedSystemArg;
	private TestResultArg testResultArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		WritableTestRun testRun = (WritableTestRun) getContext().getBean(
				getProject().getUserProperty(
						SlcAntConfig.DEFAULT_TEST_RUN_PROPERTY));

		// set overriden references
		if (testDataArg != null){
			testRun.setTestData(testDataArg.getTestData());
			log.trace("Overrides test data");
		}
		
		if (testDefinitionArg != null){
			testRun.setTestDefinition(testDefinitionArg.getTestDefinition());
			log.trace("Overrides test definition");
		}
		
		if (deployedSystemArg != null){
			testRun.setDeployedSystem(deployedSystemArg.getDeployedSystem());
			log.trace("Overrides deployed system");
		}
		
		if (testResultArg != null){
			testRun.setTestResult(testResultArg.getTestResult());
			log.trace("Overrides test result");
		}

		// notify path to test result
		TestResult result = testRun.getTestResult();
		if (result != null && result instanceof StructureAware) {
			((StructureAware) result).notifyCurrentPath(getRegistry(),
					getPath());
		}

		testRun.execute();
	}

	public TestDefinitionArg createTestDefinition() {
		testDefinitionArg = new TestDefinitionArg();
		// only test definitions can add to path
		addSAwareArg(testDefinitionArg);
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		return testDataArg;
	}

	public DeployedSystemArg createDeployedSystem() {
		deployedSystemArg = new DeployedSystemArg();
		return deployedSystemArg;
	}

	public TestResultArg createTestResult() {
		testResultArg = new TestResultArg();
		return testResultArg;
	}

}

class TestDefinitionArg extends AbstractSpringArg {
	public TestDefinition getTestDefinition() {
		return (TestDefinition) getBeanInstance();
	}
}

class TestDataArg extends AbstractSpringArg {
	public TestData getTestData() {
		return (TestData) getBeanInstance();
	}

}

class DeployedSystemArg extends AbstractSpringArg {
	public DeployedSystem getDeployedSystem() {
		return (DeployedSystem) getBeanInstance();
	}

}

class TestResultArg extends AbstractSpringArg {
	public TestResult getTestResult() {
		return (TestResult) getBeanInstance();
	}

}
