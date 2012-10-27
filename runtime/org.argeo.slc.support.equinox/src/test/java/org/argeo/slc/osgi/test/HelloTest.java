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
package org.argeo.slc.osgi.test;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.equinox.unit.AbstractOsgiRuntimeTestCase;
import org.argeo.osgi.boot.OsgiBoot;

public class HelloTest extends AbstractOsgiRuntimeTestCase {
	public void testHello() throws Exception {
		Thread.sleep(2000);
	}

	protected void installBundles() throws Exception {
		// System.out.println("java.class.path="
		// + System.getProperty("java.class.path"));

		osgiBoot.installUrls(osgiBoot.getLocationsUrls(
				OsgiBoot.DEFAULT_BASE_URL,
				System.getProperty("java.class.path")));
		osgiBoot.installUrls(osgiBoot.getBundlesUrls("src/test/bundles;in=*"));

		// Map<String, String> sysProps = new TreeMap(System.getProperties());
		// for (String key : sysProps.keySet()) {
		// System.out.println(key + "=" + sysProps.get(key));
		// }
	}

	protected List<String> getBundlesToStart() {
		List<String> bundlesToStart = new ArrayList<String>();
		// bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.slc.support.osgi.test.hello");
		return bundlesToStart;
	}

}
