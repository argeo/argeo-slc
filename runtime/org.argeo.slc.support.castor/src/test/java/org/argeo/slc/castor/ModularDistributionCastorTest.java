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
