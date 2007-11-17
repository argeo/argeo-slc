package org.argeo.slc.example.junit;

import java.io.File;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.Project;

import junit.framework.TestCase;

import org.argeo.slc.ant.AntRegistryUtil;
import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;

/** High level tests for SLC Ant. */
public class TestAntBuild extends TestCase {
	/** Tests an end-to-end Ant run. */
	public void testAllRunSimple() {
		File[] reportFiles = new File("src/test/slc/work/results/report/")
				.listFiles();
		for (File file : reportFiles) {
			file.delete();
		}

		File slcBaseDir = new File("./src/test/slc").getAbsoluteFile();

		File antFile = new File(slcBaseDir.getPath()
				+ "/root/Category1/SubCategory2/build.xml");
		Project p = AntRegistryUtil.runAll(antFile);

		ApplicationContext context = (ApplicationContext) p
				.getReference(SlcProjectHelper.REF_ROOT_CONTEXT);

		TestResultDao testResultDao = (TestResultDao) context
				.getBean("testResultDao");

		NumericTRId numericTRId = new NumericTRId();
		numericTRId.setValue(1l);
		TreeTestResult testResult1 = (TreeTestResult) testResultDao
				.getTestResult(numericTRId);
		// assertPart(testResult1, "", 0, TestStatus.PASSED, "");
		assertPart(
				testResult1,
				"#root#Category1#SubCategory2#testComplex#slc.test0#0",
				0,
				TestStatus.PASSED,
				"Sub task with path #root#Category1#SubCategory2#testComplex#slc.test0#0 executed");
		assertPart(testResult1,
				"#root#Category1#SubCategory2#testSimple#slc.test0", 1,
				TestStatus.FAILED,
				"Compare nato-expected.txt with nato-reached.txt");

		numericTRId.setValue(2l);
		TreeTestResult testResult2 = (TreeTestResult) testResultDao
				.getTestResult(numericTRId);
		assertPart(testResult2,
				"#root#Category1#SubCategory2#testSimple#slc.test2", 1,
				TestStatus.PASSED,
				"Compare eu-reform-expected.txt with eu-reform-reached.txt");
		assertPart(testResult2,
				"#root#Category1#SubCategory2#testSimple#slc.test3", 1,
				TestStatus.FAILED,
				"Compare eu-reform-expected.txt with eu-reform-reached.txt");

		assertTrue(new File("src/test/slc/work/results/report/index.html")
				.exists());
		assertTrue(new File("src/test/slc/work/results/report/1.html").exists());
		assertTrue(new File("src/test/slc/work/results/report/2.html").exists());
	}

	private void assertPart(TreeTestResult testResult, String pathStr,
			int index, Integer status, String message) {
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr, '#');
		PartSubList list = testResult.getResultParts().get(path);
		SimpleResultPart part = (SimpleResultPart) list.getParts().get(index);
		assertEquals(status, part.getStatus());
		assertEquals(message, part.getMessage());
	}
}
