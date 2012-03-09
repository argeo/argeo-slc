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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.BuildConstants;
import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.build.ModularDistributionDescriptor;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** List of distributions. */
public class ListModularDistributions extends AbstractServiceController
		implements Comparator<ModularDistributionDescriptor> {
	private Set<ModularDistribution> modularDistributions;

	private String provisioningServletPath = "/dist";

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String baseUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath()
				+ provisioningServletPath + "/";

		SortedSet<ModularDistributionDescriptor> descriptors = new TreeSet<ModularDistributionDescriptor>(
				this);

		Set<String> names = new HashSet<String>();
		Set<String> namesRelease = new HashSet<String>();

		// Scan distributions
		for (Iterator<ModularDistribution> it = modularDistributions.iterator(); it
				.hasNext();) {
			ModularDistribution md = it.next();
			ModularDistributionDescriptor mdd = fromNameVersion(baseUrl, md);

			descriptors.add(mdd);
			names.add(mdd.getName());
			if (!md.getVersion().contains(BuildConstants.SNAPSHOT))
				namesRelease.add(mdd.getName());
		}

		// Add LATESTs and RELEASEs
		for (String name : names)
			descriptors.add(fromNameVersion(baseUrl, new BasicNameVersion(name,
					BuildConstants.LATEST)));
		for (String name : namesRelease)
			descriptors.add(fromNameVersion(baseUrl, new BasicNameVersion(name,
					BuildConstants.RELEASE)));

		modelAndView.addObject(new ObjectList(descriptors));
	}

	public void setModularDistributions(
			Set<ModularDistribution> modularDistributions) {
		this.modularDistributions = modularDistributions;
	}

	public void setProvisioningServletPath(String provisioningServletPath) {
		this.provisioningServletPath = provisioningServletPath;
	}

	protected ModularDistributionDescriptor fromNameVersion(String baseUrl,
			NameVersion md) {
		String moduleBase = baseUrl + md.getName() + "/" + md.getVersion()
				+ "/";
		ModularDistributionDescriptor mdd = new ModularDistributionDescriptor();
		mdd.setName(md.getName());
		mdd.setVersion(md.getVersion());

		mdd.getModulesDescriptors().put("modularDistribution",
				moduleBase + "modularDistribution");
		mdd.getModulesDescriptors().put("eclipse", moduleBase + "site.xml");
		return mdd;

	}

	/** RELEASEs first, then LATESTs, then version */
	public int compare(ModularDistributionDescriptor mdd1,
			ModularDistributionDescriptor mdd2) {
		final int BEFORE = -1;
		final int AFTER = 1;

		String n1 = mdd1.getName();
		String v1 = mdd1.getVersion();
		String n2 = mdd2.getName();
		String v2 = mdd2.getVersion();

		if (v1.equals(BuildConstants.RELEASE))
			if (v2.equals(BuildConstants.RELEASE))
				return n1.compareTo(n2);
			else
				return BEFORE;
		else if (v2.equals(BuildConstants.RELEASE))
			return AFTER;// we know 1 not RELEASE
		else if (v1.equals(BuildConstants.LATEST))
			if (v2.equals(BuildConstants.LATEST))
				return n1.compareTo(n2);
			else
				return BEFORE;
		else if (v2.equals(BuildConstants.LATEST))
			return AFTER;// we know 1 not LATEST or RELEASE
		else if (n1.equals(n2))
			return v1.compareTo(v2);
		else
			return n1.compareTo(n2);
	}

}
