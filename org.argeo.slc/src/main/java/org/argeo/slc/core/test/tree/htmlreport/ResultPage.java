package org.argeo.slc.core.test.tree.htmlreport;

import java.io.FileWriter;
import java.io.IOException;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;

class ResultPage {
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

		buf.append("<h1>Result #").append(result.getTestResultId()).append(
				"</h1>\n");

		buf.append("<table border=1>\n");
		for (TreeSPath path : result.getResultParts().keySet()) {
			buf.append("<tr><td>");
			buf.append(path);
			if (registry != null) {
				StructureElement element = registry.getElement(path);
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
			FileWriter writer = new FileWriter(report.getResultFile(result));
			writer.write(buf.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
