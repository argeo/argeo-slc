/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.repo.osgi;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;

/**
 * Wraps an OSGi profile, simplifying access to its values such as system
 * packages, etc.
 */
public class OsgiProfile {
	public final static String PROP_SYSTEM_PACKAGES = "org.osgi.framework.system.packages";

	public final static OsgiProfile PROFILE_JAVA_SE_1_6 = new OsgiProfile(
			"JavaSE-1.6.profile");

	private final URL url;
	private final Properties properties;

	public OsgiProfile(URL url) {
		this.url = url;
		properties = new Properties();
		InputStream in = null;
		try {
			properties.load(this.url.openStream());
		} catch (Exception e) {
			throw new SlcException("Cannot initalize OSGi profile " + url, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public OsgiProfile(String name) {
		this(OsgiProfile.class.getClassLoader().getResource(
				'/'
						+ OsgiProfile.class.getPackage().getName()
								.replace('.', '/') + '/' + name));
	}

	public List<String> getSystemPackages() {
		String[] splitted = properties.getProperty(PROP_SYSTEM_PACKAGES).split(
				",");
		List<String> res = new ArrayList<String>();
		for (String pkg : splitted) {
			res.add(pkg.trim());
		}
		return Collections.unmodifiableList(res);
	}
}
