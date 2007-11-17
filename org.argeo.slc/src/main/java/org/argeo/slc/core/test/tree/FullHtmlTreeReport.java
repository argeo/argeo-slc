package org.argeo.slc.core.test.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;
import org.argeo.slc.dao.test.TestResultDao;

/**
 * Basic implementation of TestReport generating static HTML pages. If a
 * <code>TestResultDao</code> is passed, all the datas are dumped, otherwise
 * only the passed <code>TestResult</code>.
 */
public class FullHtmlTreeReport implements TestReport, StructureAware {
	private TestResultDao testResultDao;
	private TreeSRegistryDao treeSRegistryDao;
	private File reportDir;

	private StructureRegistry registry;

	public void generateTestReport(TestResult testResult) {
		
		if (testResultDao == null) {
			TreeTestResult result = (TreeTestResult) testResult;
			initRegistry(result.getResultParts().firstKey());
			generateResultPage(getResultFile(result), result);
		} else {
			if (reportDir.exists()) {
				// clean
				for (File file : reportDir.listFiles()) {
					file.delete();
				}
			}
			reportDir.mkdirs();

			StringBuffer index = new StringBuffer("");
			index
					.append("<html><header><title>Results</title></header><body>\n<table border=1>\n");

			List<TestResult> list = testResultDao.listTestResults();
			for (TestResult testRes : list) {
				TreeTestResult result = (TreeTestResult) testRes;
				initRegistry(result.getResultParts().firstKey());

				File file = getResultFile(result);
				index.append("<tr><td><a href=\"").append(file.getName())
						.append("\">");
				index.append(result.getTestResultId()).append("</a></td></tr>\n");
				generateResultPage(file, result);
			}

			index.append("</table>\n</body></html>");

			try {
				FileWriter writer = new FileWriter(reportDir.getPath()
						+ File.separator + "index.html");
				writer.write(index.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Generates a result page for one test result
	 * 
	 * @param file
	 *            file to which generate the HTML
	 * @param result
	 *            the result to dump
	 */
	protected void generateResultPage(File file, TreeTestResult result) {
		StringBuffer buf = new StringBuffer("");
		buf.append("<html>\n");
		buf.append("<header><title>Result #").append(result.getTestResultId())
				.append("</title></header>\n");

		buf.append("<body>\n");

		buf.append("<h1>Result #").append(result.getTestResultId()).append(
				"</h1>\n");

		buf.append("<table border=1>\n");
		for (TreeSPath path : result.getResultParts().keySet()) {
			buf.append("<tr><td>");
			buf.append(path);
			StructureElement element = registry.getElement(path);
			if (registry != null) {
				if (element != null) {
					buf.append("<br/><b>");
					buf.append(element.getDescription());
					buf.append("</b>");
				}
			}
			buf.append("</td>\n");
			buf.append("<td>");
			PartSubList subList = (PartSubList) result.getResultParts().get(
					path);
			buf.append("<table border=0>\n");
			for (TestResultPart part : subList.getParts()) {
				SimpleResultPart sPart = (SimpleResultPart) part;
				String color = "yellow";
				if (sPart.getStatus().equals(SimpleResultPart.PASSED)) {
					color = "green";
				} else {
					color = "red";
				}
				buf.append("<tr><td style=\"color:").append(color)
						.append("\">");

				buf.append(sPart.getMessage());
				buf.append("</td></tr>\n");
			}
			buf.append("</table>\n");

			buf.append("</td></tr>\n");
		}
		buf.append("</table>\n");

		buf.append("</body>");
		buf.append("</html>");

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(buf.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a result file location based on the report dir and the id of
	 * the test result.
	 */
	protected File getResultFile(TreeTestResult result) {
		return new File(reportDir.getPath() + File.separator
				+ result.getTestResultId() + ".html");
	}

	/** Sets the DAO to use to extract all data. */
	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	/** Sets the tree structure registry DAO.*/
	public void setTreeSRegistryDao(TreeSRegistryDao treeSRegistryDao) {
		this.treeSRegistryDao = treeSRegistryDao;
	}

	/** Sets the directory where to generate all the data. */
	public void setReportDir(File reportDir) {
		this.reportDir = reportDir;
	}

	private void initRegistry(TreeSPath path){
		if(treeSRegistryDao != null){
			registry = treeSRegistryDao.getTreeSRegistry(path);
		}
		if(registry==null){
			throw new SlcException("No structure registry available");
		}
	}
	
	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.registry = registry;
	}

}
