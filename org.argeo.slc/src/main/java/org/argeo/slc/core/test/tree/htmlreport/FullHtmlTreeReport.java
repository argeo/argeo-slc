package org.argeo.slc.core.test.tree.htmlreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hsqldb.lib.FileUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

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
			resourceToFile("style.css");

			ResultsList index = new ResultsList(this);
			List<TestResult> list = testResultDao.listTestResults();
			for (TestResult testRes : list) {
				TreeTestResult result = (TreeTestResult) testRes;

				index.addTestResult(result);
				ResultPage page = new ResultPage(this, result);
				page.generate(getRegistry(result));
			}
			index.close();
		}
	}

	/**
	 * Generates a result file location based on the report dir and the id of
	 * the test result.
	 */
	protected File getResultFile(TreeTestResult result) {
		return new File(reportDir.getPath() + File.separator + "slc-result-"
				+ result.getTestResultId() + ".html");
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
			registry = treeSRegistryDao.getTreeSRegistry(path);
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
