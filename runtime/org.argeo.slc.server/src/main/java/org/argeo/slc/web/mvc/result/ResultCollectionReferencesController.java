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

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class ResultCollectionReferencesController extends
		AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ResultCollectionReferencesController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		SortedSet<TreeTestResultCollection> results = testResultCollectionDao
				.listCollections();

		ReferenceList referenceList = new ReferenceList();
		for (TreeTestResultCollection collection : results) {
			referenceList.getReferences().add(collection.getId());
		}

		modelAndView.addObject("referenceList", referenceList);
	}
}
