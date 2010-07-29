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

import javax.servlet.http.HttpServletRequest;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sends back the results, rendered or as collection.
 */

@Controller
public class RenderingResultController {
	// private static final Log log = LogFactory
	// .getLog(RenderingResultController.class);

	public final static String MODELKEY_RESULT = "result";

	// IoC
	private TreeTestResultDao treeTestResultDao;

	@RequestMapping("/resultView.*")
	public String getPdfResultView(@RequestParam("uuid") String uuid,
			ModelMap model, HttpServletRequest request) {

		TreeTestResult result = treeTestResultDao.getTestResult(uuid);
		if (result == null)
			throw new SlcException("No result found for uuid " + uuid);
		model.addAttribute(MODELKEY_RESULT, result);

		String docType = request.getRequestURI().substring(
				request.getRequestURI().lastIndexOf(".") + 1);

		if ("pdf".equals(docType))
			return "resultPdfView";
		if ("xls".equals(docType))
			return "resultExcelView";
		if ("xslt".equals(docType))
			return "resultXsltView";
		if ("xml".equals(docType))
			return "resultXmlView";

		throw new SlcException("No renderer found for files of extension "
				+ docType);
	}

	// IoC

	public void setTreeTestResultDao(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

}