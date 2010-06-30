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

package org.argeo.slc.web.mvc.management;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class UninstallModule extends AbstractServiceController {
	private DynamicRuntime<?> dynamicRuntime;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String name = request.getParameter("name");
		String version = request.getParameter("version");
		NameVersion nameVersion = new BasicNameVersion(name, version);
		dynamicRuntime.uninstallModule(nameVersion);
	}

	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

}
