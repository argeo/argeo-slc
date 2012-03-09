/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.msg.ExecutionAnswer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class AbstractServiceController extends AbstractController {
	public final static String KEY_ANSWER = "__answer";

	private String viewName = null;

	@Override
	protected final ModelAndView handleRequestInternal(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(getViewName());
		try {
			handleServiceRequest(request, response, mv);
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			ExecutionAnswer answer = ExecutionAnswer.error(writer.toString());
			ModelAndView errorMv = new ModelAndView();
			errorMv.addObject(KEY_ANSWER, answer);
			errorMv.setViewName(KEY_ANSWER);
			return errorMv;
		}

		if (mv.getModel().size() == 0) {
			mv.addObject(KEY_ANSWER, ExecutionAnswer
					.ok("Execution completed properly"));
		}

		if (mv.getViewName() == null && mv.getModel().size() == 1)
			mv.setViewName(mv.getModel().keySet().iterator().next().toString());

		return mv;
	}

	protected abstract void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewName() {
		return viewName;
	}

}
