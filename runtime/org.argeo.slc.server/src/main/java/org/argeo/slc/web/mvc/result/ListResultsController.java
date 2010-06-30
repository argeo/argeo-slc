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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Lists results possibly filtering them. */
public class ListResultsController extends AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ListResultsController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	@SuppressWarnings(value = { "unchecked" })
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		// log.debug("userPrincipal=" + request.getUserPrincipal());
		// log.debug("authType=" + request.getAuthType());
		// log.debug("remoteUser=" + request.getRemoteUser());
		// log.debug("cookies=" + request.getCookies());
		// log.debug("requestedSessionId=" + request.getRequestedSessionId());
		// log.debug("session.id=" + request.getSession().getId());

		String collectionId = request.getParameter("collectionId");

		Map<String, String[]> parameterMap = request.getParameterMap();

		Map<String, String> attributes = new HashMap<String, String>();
		for (String parameter : parameterMap.keySet()) {
			if (parameter.startsWith("attr.")) {
				String key = parameter.substring("attr.".length());
				attributes.put(key, parameterMap.get(parameter)[0]);
			}
		}

		List<TreeTestResult> resultAttributes = testResultCollectionDao
				.listResults(collectionId, attributes);

		modelAndView.addObject("resultList", new ObjectList(resultAttributes));
	}
}
