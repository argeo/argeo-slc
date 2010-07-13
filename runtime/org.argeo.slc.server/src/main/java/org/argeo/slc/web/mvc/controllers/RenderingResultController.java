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

package org.argeo.slc.web.mvc.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.web.mvc.result.ResultExcelView;
import org.argeo.slc.web.mvc.result.ResultPdfView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Sends back the results, rendered or as collection.
 */

@Controller
public class RenderingResultController {
	private final static Log log = LogFactory.getLog(RenderingResultController.class);

	public final static String KEY_ANSWER = "__answer";
	public final static String MODELKEY_RESULT = "result";

	// IoC
	private TreeTestResultDao testResultDao;
	private ResultExcelView resultExcelView;
	private ResultPdfView resultPdfView;

	@RequestMapping("/resultView.pdf")
	public void getPdfResultView(@RequestParam(value = "uuid") String uuid,
			ModelAndView modelAndView) {
		TreeTestResult result = testResultDao.getTestResult(uuid);
		if (result == null)
			throw new SlcException("No result found for uuid " + uuid);
		modelAndView.getModelMap().addAttribute(MODELKEY_RESULT, result);
		modelAndView.setView(resultPdfView);
	}

	@RequestMapping("/resultView.xls")
	public void getXlsResultView(@RequestParam(value = "uuid") String uuid,
			ModelAndView modelAndView) {
		TreeTestResult result = testResultDao.getTestResult(uuid);
		if (result == null)
			throw new SlcException("No result found for uuid " + uuid);
		modelAndView.getModelMap().addAttribute(MODELKEY_RESULT, result);
		modelAndView.setView(resultExcelView);
	}

	public void setResultExcelView(ResultExcelView resultExcelView) {
		this.resultExcelView = resultExcelView;
	}

	public void setResultPdfView(ResultPdfView resultPdfView) {
		this.resultPdfView = resultPdfView;
	}

}
