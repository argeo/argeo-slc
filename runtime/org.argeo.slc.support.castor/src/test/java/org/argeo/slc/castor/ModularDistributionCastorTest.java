package org.argeo.slc.castor;

import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.build.ModularDistributionDescriptor;

public class ModularDistributionCastorTest extends AbstractCastorTestCase {
	public void testModularDistributionDescriptor() throws Exception {
		ModularDistributionDescriptor mdd = new ModularDistributionDescriptor();
		mdd.setName("name");
		mdd.setVersion("0.1.0");
		mdd.getModulesDescriptors().put("eclipse",
				"http://localhost/updateSite");
		mdd.getModulesDescriptors().put("modularDistribution",
				"http://localhost/modularDistribution");

		marshUnmarsh(mdd, false);
	}

	public void testModularDistributionDescriptorList() throws Exception {
		ModularDistributionDescriptor mdd = new ModularDistributionDescriptor();
		mdd.setName("name");
		mdd.setVersion("0.1.0");
		mdd.getModulesDescriptors().put("eclipse",
				"http://localhost/updateSite");
		mdd.getModulesDescriptors().put("modularDistribution",
				"http://localhost/modularDistribution");

		ModularDistributionDescriptor mdd2 = new ModularDistributionDescriptor();
		mdd2.setName("name2");
		mdd2.setVersion("0.1.1");
		mdd2.getModulesDescriptors().put("eclipse",
				"http://localhost/updateSite2");
		mdd2.getModulesDescriptors().put("modularDistribution",
				"http://localhost/modularDistribution2");

		ObjectList ol = new ObjectList();
		ol.getObjects().add(mdd);
		ol.getObjects().add(mdd2);

		marshUnmarsh(ol, false);
	}

}
