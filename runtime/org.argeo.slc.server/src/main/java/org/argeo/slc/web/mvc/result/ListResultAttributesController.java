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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class ListResultAttributesController extends AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ListResultAttributesController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String collectionId = request.getParameter("id");

		List<ResultAttributes> resultAttributes = testResultCollectionDao
				.listResultAttributes(collectionId);

		modelAndView.addObject("resultAttributesList", new ObjectList(
				resultAttributes));
	}
}
