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

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.BuildConstants;
import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ModularDistributionInterceptor extends HandlerInterceptorAdapter {
	private Set<ModularDistribution> modularDistributions;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String path = request.getPathInfo();
		StringTokenizer stS = new StringTokenizer(path, "/");
		String distributionName = stS.nextToken();
		String distributionVersion = stS.nextToken();

		SortedMap<String, ModularDistribution> choices = new TreeMap<String, ModularDistribution>();
		distribs: for (Iterator<ModularDistribution> it = modularDistributions
				.iterator(); it.hasNext();) {
			ModularDistribution md = it.next();
			if (md.getName().equals(distributionName)) {
				if (distributionVersion.equals(BuildConstants.RELEASE)
						&& md.getVersion().contains(BuildConstants.SNAPSHOT))
					continue distribs;

				else if (distributionVersion.equals(BuildConstants.LATEST))
					choices.put(md.getVersion(), md);
				else if (distributionVersion.equals(md.getVersion())) {
					choices.put(md.getVersion(), md);
					break distribs;
				}
			}
		}

		if (choices.size() == 0)
			throw new SlcException("Cannot find distribution for "
					+ new BasicNameVersion(distributionName,
							distributionVersion));

		ModularDistribution modularDistribution = choices.get(choices
				.firstKey());

		request.setAttribute("modularDistribution", modularDistribution);

		return true;
	}

	public void setModularDistributions(
			Set<ModularDistribution> modularDistributions) {
		this.modularDistributions = modularDistributions;
	}

}
