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

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.HttpRequestHandler;

/** List of distributions. */
public class AvailableDistributions implements HttpRequestHandler {
	private Set<ModularDistribution> modularDistributions;

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String baseUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath()
				+ request.getServletPath() + "/";

		Writer out = response.getWriter();

		out.write("<h1>Distributions</h1>");
		for (Iterator<ModularDistribution> it = modularDistributions.iterator(); it
				.hasNext();) {
			ModularDistribution md = it.next();
			out.write("<h2>" + md + "</h2>");
			out.write("Modules: ");
			String moduleBase = baseUrl + md.getName() + "/" + md.getVersion()
					+ "/";

			String modulesListHtml = moduleBase + "modules.html";
			out.write(" <a href=\"" + modulesListHtml + "\">html</a>");

			String modulesListPlain = moduleBase + "modules";
			out.write(" <a href=\"" + modulesListPlain + "\">plain</a>");

			String modulesListOsgiBoot = moduleBase + "osgiBoot";
			out.write(" <a href=\"" + modulesListOsgiBoot + "\">osgiBoot</a>");

			out.write("<br/>");

			out.write("Eclipse update site: ");
			String updateSiteUrl = baseUrl + md.getName() + "/"
					+ md.getVersion() + "/site.xml";
			out.write("<a href=\"" + updateSiteUrl + "\">" + updateSiteUrl
					+ "</a>");
		}
	}

	public void setModularDistributions(
			Set<ModularDistribution> modularDistributions) {
		this.modularDistributions = modularDistributions;
	}

}
