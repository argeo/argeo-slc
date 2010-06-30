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

package org.argeo.slc.web.mvc.process;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Lists SLC executions possibly filtering them. */
public class ListSlcExecutionsController extends AbstractServiceController {
	private final SlcExecutionDao slcExecutionDao;

	public ListSlcExecutionsController(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		List<SlcExecution> list = slcExecutionDao.listSlcExecutions();
		modelAndView.addObject("list", new ObjectList(list));
	}
}
