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

package org.argeo.slc.osgiboot;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * An OSGi configurator. See <a
 * href="http://wiki.eclipse.org/Configurator">http:
 * //wiki.eclipse.org/Configurator</a>
 */
public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
		osgiBoot.bootstrap();
//		try {
//			OsgiBoot.info("SLC OSGi bootstrap starting...");
//			osgiBoot.installUrls(osgiBoot.getBundlesUrls());
//			osgiBoot.installUrls(osgiBoot.getLocationsUrls());
//			osgiBoot.installUrls(osgiBoot.getModulesUrls());
//			osgiBoot.startBundles();
//			OsgiBoot.info("SLC OSGi bootstrap completed");
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
	}

	public void stop(BundleContext context) throws Exception {
	}
}
