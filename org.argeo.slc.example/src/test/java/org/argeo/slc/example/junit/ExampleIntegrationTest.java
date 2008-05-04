package org.argeo.slc.example.junit;

import java.io.File;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;

import org.argeo.slc.ant.AntRegistryUtil;
import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

/** High level tests for SLC Ant. */
public class ExampleIntegrationTest extends TestCase {
	private static Log log = LogFactory.getLog(ExampleIntegrationTest.class);

	/** Tests an end-to-end Ant run. */
	public void testAllRunSimple() throws Exception {
		String slcBase = System.getProperty("it.slc.base", "exampleSlcAppli");
		File slcBaseDir = new File(slcBase).getCanonicalFile();
		log.info("SLC base: " + slcBaseDir);

		String reportDirPath = slcBaseDir.getPath() + "/work/results/report/";
		File[] reportFiles = new File(reportDirPath).listFiles();
		for (File file : reportFiles) {
			file.delete();
		}

		File antFile = new File(slcBaseDir.getPath()
				+ "/root/Category1/SubCategory2/build.xml");
		Project p = AntRegistryUtil.runAll(antFile);

		ApplicationContext context = (ApplicationContext) p
				.getReference(SlcProjectHelper.REF_ROOT_CONTEXT);

		TestResultDao testResultDao = (TestResultDao) context
				.getBean("testResultDao");

		TreeTestResult testResult1 = (TreeTestResult) testResultDao
				.getTestResult("1");
		// assertPart(testResult1, "", 0, TestStatus.PASSED, "");
		UnitTestTreeUtil
				.assertPart(
						testResult1,
						"/root/Category1/SubCategory2/testComplex/slc.test0/0",
						0,
						TestStatus.PASSED,
						"Sub task with path /root/Category1/SubCategory2/testComplex/slc.test0/0 executed");
		UnitTestTreeUtil.assertPart(testResult1,
				"/root/Category1/SubCategory2/testSimple/slc.test0", 1,
				TestStatus.FAILED,
				"Compare nato-expected.txt with nato-reached.txt");
		UnitTestTreeUtil.assertPart(testResult1,
				"/root/Category1/SubCategory2/testError/slc.test0", 0,
				TestStatus.ERROR, "Execute example appli");

//		TreeTestResult testResult2 = (TreeTestResult) testResultDao
//				.getTestResult("2");
//		UnitTestTreeUtil.assertPart(testResult2,
//				"/root/Category1/SubCategory2/testSimple/slc.test2", 1,
//				TestStatus.PASSED,
//				"Compare eu-reform-expected.txt with eu-reform-reached.txt");
//		UnitTestTreeUtil.assertPart(testResult2,
//				"/root/Category1/SubCategory2/testSimple/slc.test3", 1,
//				TestStatus.FAILED,
//				"Compare eu-reform-expected.txt with eu-reform-reached.txt");

		assertTrue(new File(reportDirPath + "index.html").exists());
		assertTrue(new File(reportDirPath + "slc-resultsList.html").exists());
		assertTrue(new File(reportDirPath + "slc-result-1.html").exists());
//		assertTrue(new File(reportDirPath + "slc-result-2.html").exists());
	}

}
