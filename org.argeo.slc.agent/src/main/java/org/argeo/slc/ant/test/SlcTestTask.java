package org.argeo.slc.ant.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.argeo.slc.ant.AntConstants;
import org.argeo.slc.ant.spring.SpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.ExecutableTestRun;
import org.argeo.slc.core.test.SimpleTestResult;
import org.argeo.slc.core.test.SimpleTestRun;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.WritableTestRun;
import org.argeo.slc.spring.SpringUtils;
import org.springframework.beans.BeansException;

/** Ant task wrapping a test run. */
public class SlcTestTask extends SAwareTask {
	private Log log = LogFactory.getLog(SlcTestTask.class);

	private String testRunBean = null;

	private SpringArg<TestDefinition> testDefinitionArg;
	private SpringArg<TestData> testDataArg;
	private SpringArg<DeployedSystem> deployedSystemArg;
	private SpringArg<TestResult> testResultArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		// find test run
		final String testRunBeanT;
		if (testRunBean != null) {
			testRunBeanT = testRunBean;
		} else {
			testRunBeanT = getProject().getProperty(
					AntConstants.DEFAULT_TEST_RUN_PROPERTY);
		}
		WritableTestRun testRun = null;

		if (testRunBeanT != null) {
			try {
				testRun = (WritableTestRun) getContext().getBean(testRunBeanT);
				if (log.isTraceEnabled())
					log.trace("Load test run bean from bean name "
							+ testRunBeanT);
			} catch (BeansException e) {
				// silent, will try defaults
			}
		}

		if (testRun == null) {
			testRun = loadSingleFromContext(WritableTestRun.class);
			if (testRun == null) {
				testRun = new SimpleTestRun();
				log.trace("Created default simple test run");
			} else {
				if (log.isTraceEnabled())
					log.trace("Load test run from scanning Spring context");
			}
		}

		// set overridden references
		if (testDataArg != null) {
			testRun.setTestData(testDataArg.getBeanInstance());
			log.trace("Overrides test data");
		}

		if (testDefinitionArg != null) {
			testRun.setTestDefinition(testDefinitionArg.getBeanInstance());
			log.trace("Overrides test definition");
		}

		if (deployedSystemArg != null) {
			testRun.setDeployedSystem(deployedSystemArg.getBeanInstance());
			log.trace("Overrides deployed system");
		}

		if (testResultArg != null) {
			testRun.setTestResult(testResultArg.getBeanInstance());
			log.trace("Overrides test result");
		}

		// notify path to test result
		TestResult result = testRun.getTestResult();
		if (result == null) {
			result = loadSingleFromContext(TestResult.class);
			if (result == null) {
				result = new SimpleTestResult();
				log.warn("Created default simple test result");
			} else {
				if (log.isTraceEnabled())
					log.trace("Load test result from scanning Spring context");
			}
			testRun.setTestResult(result);
		}

		SlcExecution slcExecution = getSlcExecution();
		testRun.notifySlcExecution(slcExecution);

		if (result != null && result instanceof StructureAware) {
			((StructureAware<TreeSPath>) result).notifyCurrentPath(
					getRegistry(), getTreeSPath());
		}

		((ExecutableTestRun) testRun).execute();
	}

	/**
	 * The bean name of the test run to use. If not set the default is used.
	 * 
	 * @see SlcAntConfig
	 */
	public void setTestRun(String testRunBean) {
		this.testRunBean = testRunBean;
	}

	/** Creates sub tag. */
	public SpringArg<TestDefinition> createTestDefinition() {
		testDefinitionArg = new SpringArg<TestDefinition>();
		// only test definitions can add to path
		addSAwareArg(testDefinitionArg);
		return testDefinitionArg;
	}

	/** Creates sub tag. */
	public SpringArg<TestData> createTestData() {
		testDataArg = new SpringArg<TestData>();
		return testDataArg;
	}

	/** Creates sub tag. */
	public SpringArg<DeployedSystem> createDeployedSystem() {
		deployedSystemArg = new SpringArg<DeployedSystem>();
		return deployedSystemArg;
	}

	/** Creates sub tag. */
	public SpringArg<TestResult> createTestResult() {
		testResultArg = new SpringArg<TestResult>();
		return testResultArg;
	}

	protected <T> T loadSingleFromContext(Class<T> clss) {
		return SpringUtils.loadSingleFromContext(getContext(), clss);
	}
}
