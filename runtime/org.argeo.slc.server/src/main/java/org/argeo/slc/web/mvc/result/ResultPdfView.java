package org.argeo.slc.web.mvc.result;

import java.awt.Color;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SlcTestUtils;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class ResultPdfView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map model, Document document,
			PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		TreeTestResult ttr = (TreeTestResult) model
				.get(ResultViewController.MODELKEY_RESULT);

		document.addTitle("Result " + ttr.getUuid());
		document.add(new Paragraph("Result " + ttr.getUuid()));

		for (TreeSPath path : ttr.getResultParts().keySet()) {
			PartSubList lst = ttr.getResultParts().get(path);
			document.add(new Paragraph("Path " + path));
			Table table = new Table(2, lst.getParts().size());
			for (TestResultPart part : lst.getParts()) {
				Integer status = part.getStatus();
				Cell statusCell = new Cell(SlcTestUtils.statusToString(status));
				final Color color;
				if (status.equals(TestStatus.PASSED))
					color = Color.GREEN;
				else if (status.equals(TestStatus.FAILED))
					color = Color.RED;
				else
					color = Color.MAGENTA;

				statusCell.setBackgroundColor(color);
				table.addCell(statusCell);
				table.addCell(part.getMessage());
			}
			document.add(table);
		}

	}

}
