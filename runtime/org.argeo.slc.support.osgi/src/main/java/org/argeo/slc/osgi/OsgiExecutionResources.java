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

package org.argeo.slc.osgi;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.FileExecutionResources;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.Resource;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.io.OsgiBundleResource;

/** Write access to resources in an OSGi context */
public class OsgiExecutionResources extends FileExecutionResources implements
		BundleContextAware {
	private final static Log log = LogFactory
			.getLog(OsgiExecutionResources.class);

	private BundleContext bundleContext;

	@Override
	protected File fileFromResource(Resource resource) {
		File file = super.fileFromResource(resource);
		if (file != null)
			return file;

		if (!(resource instanceof OsgiBundleResource))
			return null;

		OsgiBundleResource osgiBundleResource = (OsgiBundleResource) resource;
		try {
			return osgiBundleResource.getFile();
		} catch (IOException e) {
			if (log.isTraceEnabled())
				log.trace("Resource " + resource
						+ " is not available on the file system: " + e);
		}

		// TODO: ability to access resources in other bundles
		String location = bundleContext.getBundle().getLocation();
		String base = null;
		if (location.startsWith("reference:file:"))
			base = location.substring("reference:file:".length());
		else if (location.startsWith("initial@reference:file:")) {
			// TODO: Equinox specific?
			String relPath = location.substring("initial@reference:file:"
					.length());
			// if (relPath.startsWith("../"))// relative to the framework jar
			// relPath = relPath.substring("../".length());
			// String framework =
			// System.getProperty("osgi.framework").substring(
			// "file:".length());
			// log.debug(framework);
			String installArea = System.getProperty("osgi.install.area")
					.substring("file:".length());
			// log.debug(installArea);
			base = installArea + '/' + relPath;
			// int sepIndex = framework.lastIndexOf(File.separatorChar);
			// framework = framework.substring(0, sepIndex);
			// base = framework + '/' + relPath;
		} else {
			return null;
		}

		String path = base + '/' + osgiBundleResource.getPathWithinContext();
		try {
			file = new File(path).getCanonicalFile();
		} catch (IOException e) {
			throw new SlcException("Cannot determine canonical path for "
					+ path, e);
		}

		if (!file.exists())
			throw new SlcException(file
					+ " was retrieved in bundle located at '" + location
					+ "' for resource " + resource + " but it does not exist");

		if (log.isTraceEnabled())
			log.debug("OSGi local resource: " + file + " from " + resource);
		return file;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
