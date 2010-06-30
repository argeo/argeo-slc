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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.services.TestManagerService;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copy from a collection to another based on Spring simple pattern matching.
 * 
 * @see PatternMatchUtils
 */
public class CopyCollectionToCollectionController extends
		AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;
	private final TestManagerService testManagerService;

	public CopyCollectionToCollectionController(
			TreeTestResultCollectionDao testResultCollectionDao,
			TestManagerService testManagerService) {
		this.testResultCollectionDao = testResultCollectionDao;
		this.testManagerService = testManagerService;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String sourceCollectionId = request.getParameter("sourceCollectionId");
		String targetCollectionId = request.getParameter("targetCollectionId");
		String[] attrNames = request.getParameterValues("attrName");
		String[] attrPatterns = request.getParameterValues("attrPattern");

		// Checks
		if (sourceCollectionId == null || targetCollectionId == null)
			throw new SlcException(
					"Source and target collection ids must be specified");
		if (attrNames != null
				&& (attrPatterns == null || attrNames.length != attrPatterns.length))
			throw new SlcException(
					"There must be as many attrName as attrPatterns");

		TreeTestResultCollection sourceCollection = testResultCollectionDao
				.getTestResultCollection(sourceCollectionId);
		if (attrNames != null) {
			int index = 0;
			for (String attrName : attrNames) {
				String attrPattern = attrPatterns[index];// safe: checked above

				for (TreeTestResult treeTestResult : sourceCollection
						.getResults()) {
					if (PatternMatchUtils.simpleMatch(attrPattern,
							treeTestResult.getAttributes().get(attrName))) {
						testManagerService.addResultToCollection(
								targetCollectionId, treeTestResult.getUuid());
					}
				}
				index++;
			}
		} else {
			// remove all
			// TODO: optimize
			for (TreeTestResult treeTestResult : sourceCollection.getResults()) {
				testManagerService.addResultToCollection(targetCollectionId,
						treeTestResult.getUuid());
			}
		}
	}
}
