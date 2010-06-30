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

import java.util.ArrayList;
import java.util.List;

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

public class RemoveResultFromCollectionController extends
		AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;
	private final TestManagerService testManagerService;

	public RemoveResultFromCollectionController(
			TreeTestResultCollectionDao testResultCollectionDao,
			TestManagerService testManagerService) {
		this.testResultCollectionDao = testResultCollectionDao;
		this.testManagerService = testManagerService;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String collectionId = request.getParameter("collectionId");
		String[] resultUuids = request.getParameterValues("resultUuid");
		String[] attrNames = request.getParameterValues("attrName");
		String[] attrPatterns = request.getParameterValues("attrPattern");

		// Checks
		if (collectionId == null)
			throw new SlcException("A collection id must be specified");
		if (attrNames != null
				&& (attrPatterns == null || attrNames.length != attrPatterns.length))
			throw new SlcException(
					"There must be as many attrName as attrPatterns");

		// Remove specified results
		if (resultUuids != null)
			for (String resultUuid : resultUuids)
				testManagerService.removeResultFromCollection(collectionId,
						resultUuid);

		if (attrNames != null) {
			TreeTestResultCollection sourceCollection = testResultCollectionDao
					.getTestResultCollection(collectionId);

			int index = 0;
			for (String attrName : attrNames) {
				String attrPattern = attrPatterns[index];// safe: checked above

				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					if (PatternMatchUtils.simpleMatch(attrPattern,
							treeTestResult.getAttributes().get(attrName))) {
						testManagerService.removeResultFromCollection(
								collectionId, treeTestResult.getUuid());
					}
				}
				index++;
			}
		} else {
			if (resultUuids == null) {// no specs
				// remove all
				// TODO: optimize
				TreeTestResultCollection sourceCollection = testResultCollectionDao
						.getTestResultCollection(collectionId);
				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					testManagerService.removeResultFromCollection(collectionId,
							treeTestResult.getUuid());
				}

			}
		}

	}
}
