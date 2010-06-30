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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.test.TestRunDescriptor;

public class TestRunViewController extends ParameterizableViewController {
	private final TestRunDescriptorDao testRunDescriptorDao;

	public TestRunViewController(TestRunDescriptorDao testRunDescriptorDao) {
		this.testRunDescriptorDao = testRunDescriptorDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uuid = request.getParameter("uuid");
		TestRunDescriptor testRunDescriptor = testRunDescriptorDao
				.getTestRunDescriptor(uuid);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("testRunDescriptor", testRunDescriptor);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}

}
