package org.argeo.slc.core.test.tree.htmlreport;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;

class ResultPage {
	private final static Log log = LogFactory.getLog(ResultPage.class);

	private final FullHtmlTreeReport report;
	private final TreeTestResult result;

	ResultPage(FullHtmlTreeReport report, TreeTestResult result) {
		this.report = report;
		this.result = result;
	}

	/**
	 * Generates a result page for one test result
	 * 
	 * @param file
	 *            file to which generate the HTML
	 * @param result
	 *            the result to dump
	 */
	protected void generate(StructureRegistry registry) {
		StringBuffer buf = new StringBuffer("");
		buf.append("<html>\n");
		buf.append("<header>");
		buf.append("<title>Result #").append(result.getTestResultId()).append(
				"</title>\n");
		buf
				.append("<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\"/>");
		buf.append("</header>\n");

		buf.append("<body>\n");

		// Header
		buf.append("<h1>Result #").append(result.getTestResultId()).append(
				"</h1>\n");
		buf.append(report.sdf.format(result.getCloseDate()));

		// TOC
		generateToc(buf, registry);

		generatePartsList(buf, registry);

		buf.append("</body>");
		buf.append("</html>");

		try {
			FileUtils.writeStringToFile(report.getResultFile(result), buf
					.toString());
		} catch (IOException e) {
			log.error("Could not save result page.", e);
		}
	}

	private void generateToc(StringBuffer buf, StructureRegistry registry) {
		buf.append("<h2>Overview</h2>\n");
		SortedMap<TreeSPath, Integer> toc = new TreeMap<TreeSPath, Integer>();
		for (TreeSPath path : result.getResultParts().keySet()) {
			PartSubList subList = (PartSubList) result.getResultParts().get(
					path);
			boolean isFailed = false;
			for (TestResultPart part : subList.getParts()) {
				if (!part.getStatus().equals(TestStatus.PASSED)) {
					isFailed = true;
					break;
				}
			}
			fillToc(toc, path, isFailed);
		}

		buf.append("<table border=\"0\">\n");
		for (TreeSPath path : toc.keySet()) {
			boolean inResult = result.getResultParts().containsKey(path);
			boolean isFailed = !toc.get(path).equals(TestStatus.PASSED);

			buf.append("<tr><td class=\"").append(
					isFailed ? "failed" : "passed").append("\">");
			int depth = path.depth();
			for (int i = 0; i < depth; i++) {
				buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}

			if (inResult) {
				buf.append("<a href=\"#").append(anchor(path)).append(
						"\" class=\"").append(isFailed ? "failed" : "passed")
						.append("\"><b>");
			}
			if (registry != null) {
				StructureElement element = registry.getElement(path);
				if (element != null) {
					buf.append(element.getDescription());
				} else {
					buf.append(path.getName());
				}
			}
			if (inResult) {
				buf.append("</b></a>");
			}
			buf.append("</td></tr>\n");
		}
		buf.append("</table>\n");
		buf.append("<hr/>\n");
	}

	private void generatePartsList(StringBuffer buf, StructureRegistry registry) {
		for (TreeSPath path : result.getResultParts().keySet()) {
			buf.append("<p>\n");
			buf.append("<a name=\"").append(anchor(path)).append("\"></a>");
			buf.append("<h2>");
			String description = path.getName();
			if (registry != null) {
				StructureElement element = registry.getElement(path);
				if (element != null) {
					description = element.getDescription();
				}
			}
			buf.append(description);
			buf.append("</h2>");

			PartSubList subList = (PartSubList) result.getResultParts().get(
					path);
			buf.append("<table border=0>\n");
			for (TestResultPart part : subList.getParts()) {
				SimpleResultPart sPart = (SimpleResultPart) part;
				String clss = "";
				if (sPart.getStatus().equals(SimpleResultPart.PASSED)) {
					clss = "passed";
				} else {
					clss = "failed";
				}
				buf.append("<tr><td class=\"").append(clss).append("\">");

				buf.append(sPart.getMessage());
				buf.append("</td></tr>\n");
			}
			buf.append("</table>\n");
		}
	}

	private void fillToc(SortedMap<TreeSPath, Integer> toc, TreeSPath path,
			boolean isFailed) {
		if (isFailed) {
			toc.put(path, TestStatus.FAILED);
		} else {
			if (!toc.containsKey(path)) {
				toc.put(path, TestStatus.PASSED);
			}
		}

		if (path.getParent() != null) {
			fillToc(toc, path.getParent(), isFailed);
		}
	}

	private String anchor(TreeSPath path) {
		return path.getAsUniqueString().replace(path.getSeparator(), '_');
	}

	private String describedPath(TreePath path, StructureRegistry registry) {
		StringBuffer buf = new StringBuffer("");
		return buf.toString();
	}
}
