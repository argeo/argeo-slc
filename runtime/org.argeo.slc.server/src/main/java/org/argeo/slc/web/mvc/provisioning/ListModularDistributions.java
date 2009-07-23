package org.argeo.slc.web.mvc.provisioning;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.build.ModularDistributionDescriptor;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** List of distributions. */
public class ListModularDistributions extends AbstractServiceController {
	private Set<ModularDistribution> modularDistributions;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String baseUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/dist"
				+ "/";

		ObjectList ol = new ObjectList();

		for (Iterator<ModularDistribution> it = modularDistributions.iterator(); it
				.hasNext();) {
			ModularDistribution md = it.next();
			String moduleBase = baseUrl + md.getName() + "/" + md.getVersion()
					+ "/";
			ModularDistributionDescriptor mdd = new ModularDistributionDescriptor();
			mdd.setName(md.getName());
			mdd.setVersion(md.getVersion());

			mdd.getModulesDescriptors().put("modularDistribution",
					moduleBase + "modularDistribution");
			mdd.getModulesDescriptors().put("eclipse", moduleBase + "site.xml");

			ol.getObjects().add(mdd);
		}

		modelAndView.addObject(ol);
	}

	public void setModularDistributions(
			Set<ModularDistribution> modularDistributions) {
		this.modularDistributions = modularDistributions;
	}

}
