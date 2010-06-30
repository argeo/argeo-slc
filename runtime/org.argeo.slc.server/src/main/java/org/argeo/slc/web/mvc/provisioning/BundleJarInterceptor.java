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

package org.argeo.slc.web.mvc.provisioning;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.SlcException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BundleJarInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String path = request.getPathInfo();
		StringTokenizer stS = new StringTokenizer(path, "/");
		String fileName = null;
		while (stS.hasMoreTokens()) {
			String token = stS.nextToken();
			if (!stS.hasMoreTokens()) {
				fileName = token;
			}
		}

		int ind_ = fileName.indexOf('-');
		String moduleName;
		if (ind_ > -1)
			moduleName = fileName.substring(0, ind_);
		else
			throw new SlcException("Cannot determine version for " + fileName);

		String versionAndExtension = fileName.substring(ind_ + 1);
		int indExt = versionAndExtension.lastIndexOf('.');
		String moduleVersion = versionAndExtension.substring(0, indExt);

		request.setAttribute("moduleName", moduleName);
		request.setAttribute("moduleVersion", moduleVersion);

		return true;
	}
}
