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

package org.argeo.slc.web.mvc.result;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SlcTestUtils;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultPart;
import org.springframework.web.servlet.view.document.AbstractJExcelView;

public class ResultExcelView extends AbstractJExcelView {
	public final static String MODELKEY_RESULT = "result";

	
	@SuppressWarnings(value = { "unchecked" })
	protected void buildExcelDocument(Map model, WritableWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			if (workbook.getNumberOfSheets() == 0) {
				workbook.createSheet("SLC", 0);
			}
			WritableSheet sheet = workbook.getSheet("SLC");

			TreeTestResult ttr = (TreeTestResult) model
					.get(MODELKEY_RESULT);

			sheet.addCell(new Label(0, 0, "Result " + ttr.getUuid()));

			int currentRow = 1;
			for (TreeSPath path : ttr.getResultParts().keySet()) {
				PartSubList lst = ttr.getResultParts().get(path);
				sheet.addCell(new Label(0, currentRow, "Path " + path));
				currentRow++;
				for (TestResultPart part : lst.getParts()) {
					sheet.addCell(new Label(0, currentRow, SlcTestUtils
							.statusToString(part.getStatus())));
					sheet.addCell(new Label(1, currentRow, part.getMessage()));
					currentRow++;
				}
				currentRow++;// add an empty line between paths
			}
		} catch (Exception e) {
			throw new SlcException("Could not write spreadsheet.", e);
		}
	}
}
