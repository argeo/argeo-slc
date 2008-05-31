package org.argeo.slc.core.test.tree.htmlreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;
import org.argeo.slc.dao.test.TestResultDao;

/**
 * Basic implementation of TestReport generating static HTML pages. If a
 * <code>TestResultDao</code> is passed, all the data is dumped, otherwise
 * only the passed <code>TestResult</code>.
 */
public class FullHtmlTreeReport implements TestReport, StructureAware {
	private static final Log log = LogFactory.getLog(FullHtmlTreeReport.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private TestResultDao testResultDao;
	private TreeSRegistryDao treeSRegistryDao;
	private File reportDir;

	private StructureRegistry localRegistry;

	public void generateTestReport(TestResult testResult) {

		if (testResultDao == null) {
			if (testResult == null)
				throw new SlcException(
						"Cannot generate report without DAO or result instance.");

			TreeTestResult result = (TreeTestResult) testResult;
			ResultPage page = new ResultPage(this, result);
			page.generate(getRegistry(result));
		} else {
			if (reportDir.exists()) {
				// clean
				for (File file : reportDir.listFiles()) {
					file.delete();
				}
			}
			reportDir.mkdirs();

			resourceToFile("index.html");

			ResultsList index = new ResultsList(this);
			List<TestResult> list = testResultDao.listTestResults();
			SortedSet<TestResult> sortedSet = new TreeSet<TestResult>(
					new Comparator<TestResult>() {

						public int compare(TestResult o1, TestResult o2) {
							if (o1.getCloseDate() == null
									|| o2.getCloseDate() == null)
								return 0;
							// inverse date order (last first)
							return o2.getCloseDate().compareTo(
									o1.getCloseDate());
						}

					});
			sortedSet.addAll(list);
			for (TestResult testRes : sortedSet) {
				TreeTestResult result = (TreeTestResult) testRes;

				index.addTestResult(result);
				ResultPage page = new ResultPage(this, result);
				page.generate(getRegistry(result));
			}
			index.close();
		}
		log.info("Generated HTML test result report to "+reportDir);
	}

	/**
	 * Generates a result file location based on the report dir and the id of
	 * the test result.
	 */
	protected File getResultFile(TreeTestResult result) {
		return new File(reportDir.getPath() + File.separator + "slc-result-"
				+ result.getUuid() + ".html");
	}

	/** Sets the DAO to use to extract all data. */
	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	/** Sets the tree structure registry DAO. */
	public void setTreeSRegistryDao(TreeSRegistryDao treeSRegistryDao) {
		this.treeSRegistryDao = treeSRegistryDao;
	}

	/** Sets the directory where to generate all the data. */
	public void setReportDir(File reportDir) {
		this.reportDir = reportDir;
	}

	private StructureRegistry getRegistry(TreeTestResult result) {
		StructureRegistry registry = null;
		if (treeSRegistryDao != null) {
			TreeSPath path = result.getResultParts().firstKey();
			registry = treeSRegistryDao.getActiveTreeSRegistry();
		}
		if (registry == null) {
			registry = localRegistry;
		}
		if (registry == null) {
			throw new SlcException("No structure registry available");
		}
		return registry;
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.localRegistry = registry;
	}

	File getReportDir() {
		return reportDir;
	}

	void addStyles(StringBuffer buf) {
		try {
			buf.append("<style type=\"text/css\">\n");
			InputStream in = FullHtmlTreeReport.class
					.getResourceAsStream("style.css");
			String styles = IOUtils.toString(in);
			IOUtils.closeQuietly(in);
			buf.append(styles);
			buf.append("\n</style>\n");
		} catch (IOException e) {
			throw new SlcException("Cannot load styles", e);
		}
	}

	private void resourceToFile(String resourceName) {
		try {
			File file = new File(getReportDir() + File.separator + resourceName);
			InputStream in = FullHtmlTreeReport.class
					.getResourceAsStream(resourceName);
			FileOutputStream out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (Exception e) {
			throw new SlcException("Cannot load resource", e);
		}

	}

}
