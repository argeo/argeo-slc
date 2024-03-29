package org.argeo.slc.cms.distribution;

import org.argeo.api.a2.A2Module;
import org.argeo.api.slc.build.Distribution;

public class A2ModuleDistribution implements Distribution {
	private A2Module a2Module;

	@Override
	public String getDistributionId() {
		return a2Module.getCoordinates();
	}

}
