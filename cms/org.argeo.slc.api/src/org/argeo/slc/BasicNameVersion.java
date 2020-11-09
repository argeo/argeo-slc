package org.argeo.slc;

import java.io.Serializable;

/** @deprecated use {@link DefaultNameVersion} instead. */
@Deprecated
public class BasicNameVersion extends DefaultNameVersion implements
		Serializable {
	private static final long serialVersionUID = -5127304279136195127L;

	public BasicNameVersion() {
	}

	/** Interprets string in OSGi-like format my.module.name;version=0.0.0 */
	public BasicNameVersion(String nameVersion) {
		int index = nameVersion.indexOf(";version=");
		if (index < 0) {
			setName(nameVersion);
			setVersion(null);
		} else {
			setName(nameVersion.substring(0, index));
			setVersion(nameVersion.substring(index + ";version=".length()));
		}
	}

	public BasicNameVersion(String name, String version) {
		super(name, version);
	}

	public BasicNameVersion(NameVersion nameVersion) {
		super(nameVersion);
	}
}
