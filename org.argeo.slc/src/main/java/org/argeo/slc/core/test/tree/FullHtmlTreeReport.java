package org.argeo.slc.core.test.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestReport;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.dao.test.TestResultDao;

public class FullHtmlTreeReport implements TestReport, StructureAware {
	private TestResultDao testResultDao;
	private File reportDir;

	private StructureRegistry registry;

	public void generateTestReport(TestResult testResult) {
		if (testResultDao == null) {
			TreeTestResult result = (TreeTestResult) testResult;
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
				TreeTestResult res = (TreeTestResult) testRes;

				File file = getResultFile(res);
				index.append("<tr><td><a href=\"").append(file.getName())
						.append("\">");
				index.append(res.getTestResultId()).append("</a></td></tr>\n");
				generateResultPage(file, res);
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
				if (sPart.getStatus() == SimpleResultPart.PASSED) {
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

	protected File getResultFile(TreeTestResult result) {
		return new File(reportDir.getPath() + File.separator
				+ result.getTestResultId() + ".html");
	}

	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	public void setReportDir(File reportDir) {
		this.reportDir = reportDir;
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.registry = registry;
	}

}
