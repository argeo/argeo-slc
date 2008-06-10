package org.argeo.slc.example.junit;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.argeo.slc.ant.AntRegistryUtil;
import org.argeo.slc.ant.SlcAntConstants;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.springframework.context.ApplicationContext;

public class ContextIntegrationTest extends TestCase {
	private static Log log = LogFactory.getLog(ContextIntegrationTest.class);

	public void testContext() throws Exception {
		TestResultDao<TreeTestResult> testResultDao = runAnt(
				"root/Context/build.xml", null);

		TreeTestResult testResult1 = (TreeTestResult) testResultDao
				.getTestResult("1");

		String basePath = "/root/Context/testContext/";
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test0/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/reference2", 0, TestStatus.PASSED,
				"Values matched for key 'reference2'");
		UnitTestTreeUtil.assertPart(testResult1, basePath + "slc.test1/var", 0,
				TestStatus.PASSED, "Values matched for key 'var'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varIntern", 0, TestStatus.PASSED,
				"Values matched for key 'varIntern'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varExtern", 0, TestStatus.PASSED,
				"Values matched for key 'varExtern'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/greeting", 0, TestStatus.PASSED,
				"Values matched for key 'greeting'");

	}

	public void testBaseContext() throws Exception {
		TestResultDao<TreeTestResult> testResultDao = runAnt(
				"root/Context/build.xml", "testBaseContext");

		TreeTestResult testResult1 = (TreeTestResult) testResultDao
				.getTestResult("1");

		String basePath = "/root/Context/testBaseContext/";
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test0/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varIntern", 0, TestStatus.PASSED,
				"Values matched for key 'varIntern'");

	}

	private TestResultDao<TreeTestResult> runAnt(String antFileRelPath,
			String target) throws Exception {
		String slcBase = System.getProperty("it.slc.base", "exampleSlcAppli");
		File slcBaseDir = new File(slcBase).getCanonicalFile();
		log.info("SLC base: " + slcBaseDir);

		File antFile = new File(slcBaseDir.getPath() + File.separator
				+ antFileRelPath);
		Project p = AntRegistryUtil.runAll(antFile, target);

		ApplicationContext context = (ApplicationContext) p
				.getReference(SlcAntConstants.REF_ROOT_CONTEXT);

		TestResultDao<TreeTestResult> testResultDao = (TestResultDao<TreeTestResult>) context
				.getBean("testResultDao");
		return testResultDao;

	}
}
