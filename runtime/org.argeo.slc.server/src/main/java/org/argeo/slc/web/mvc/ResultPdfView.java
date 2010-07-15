/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.web.mvc;

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
	// private static final Log log = LogFactory.getLog(ResultPdfView.class);

	public final static String MODELKEY_RESULT = "result";

	@Override
	@SuppressWarnings(value = { "unchecked" })
	protected void buildPdfDocument(Map model, Document document,
			PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		TreeTestResult ttr = (TreeTestResult) model.get(MODELKEY_RESULT);

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
