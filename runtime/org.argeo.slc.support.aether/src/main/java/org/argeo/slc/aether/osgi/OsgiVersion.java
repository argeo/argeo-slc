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
package org.argeo.slc.aether.osgi;

import org.osgi.framework.Version;

/**
 * Wraps an OSGi {@link Version} as an Aether
 * {@link org.sonatype.aether.version.Version}.
 */
public class OsgiVersion implements org.sonatype.aether.version.Version {
	final private Version version;

	public OsgiVersion(String str) {
		version = Version.parseVersion(str);
	}

	public Version getVersion() {
		return version;
	}

	public int compareTo(org.sonatype.aether.version.Version v) {
		if (!(v instanceof OsgiVersion))
			return 0;
		OsgiVersion ov = (OsgiVersion) v;
		return version.compareTo(ov.version);
	}
}
