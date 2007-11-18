package org.argeo.slc.core.test.tree.htmlreport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.argeo.slc.core.test.tree.TreeTestResult;

class ResultsList {
	private final FullHtmlTreeReport report;
	private final StringBuffer buf = new StringBuffer("");

	ResultsList(FullHtmlTreeReport report) {
		this.report = report;

		buf.append("<html><header><title>Results</title></header><body>");
		buf.append("<header>");
		buf.append("<title>Results</title>\n");
		report.addStyles(buf);
		buf.append("</header>\n");
		buf.append("<body>\n");

		buf.append("<h1>Results</h1>\n");
		buf.append("<table border=\"0\" cellspacing=\"1\">\n");
		buf.append("<tr><th>Date</th><th>Result Id</th></tr>\n");
	}

	void addTestResult(TreeTestResult result) {
		buf.append("<tr>\n");
		// Date
		buf.append("<td>");
		Date closeDate = result.getCloseDate();
		if (closeDate == null) {
			buf.append("[Not closed]");
		} else {
			buf.append(report.sdf.format(closeDate));
		}
		buf.append("</td>\n");
		// Id and link
		buf.append("<td><a class=\"nav\" href=\"");
		buf.append(report.getResultFile(result).getName());
		buf.append("\" target=\"main\">#");
		buf.append(result.getTestResultId()).append("</a></td>\n");

		buf.append("</tr>\n");
	}

	void close() {
		buf.append("</table>\n</body></html>");

		try {
			FileWriter writer = new FileWriter(report.getReportDir().getPath()
					+ File.separator + "slc-resultsList.html");
			writer.write(buf.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
