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
