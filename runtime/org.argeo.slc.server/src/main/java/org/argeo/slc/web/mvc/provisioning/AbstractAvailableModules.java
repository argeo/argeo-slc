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
package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.NameVersion;
import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.HttpRequestHandler;

/** List of modules for a distribution. */
public abstract class AbstractAvailableModules implements HttpRequestHandler {
	protected abstract void print(Writer out, String baseUrl,
			ModularDistribution md) throws IOException;

	public final void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(getContentType());

		ModularDistribution md = (ModularDistribution) request
				.getAttribute("modularDistribution");

		String baseUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath()
				+ request.getServletPath() + "/" + md.getName() + "/"
				+ md.getVersion() + "/";

		print(response.getWriter(), baseUrl, md);
	}

	public String getContentType() {
		return "text/plain";
	}

	protected String jarUrl(String baseUrl, NameVersion nameVersion) {
		return baseUrl + jarFileName(nameVersion);
	}

	protected String jarFileName(NameVersion nameVersion) {
		return nameVersion.getName() + "-" + nameVersion.getVersion() + ".jar";
	}

}
