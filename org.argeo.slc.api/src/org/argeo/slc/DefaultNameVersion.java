package org.argeo.slc;


/** Canonical implementation of {@link NameVersion} */
public class DefaultNameVersion implements NameVersion,
		Comparable<NameVersion> {
	private String name;
	private String version;

	public DefaultNameVersion() {
	}

	/** Interprets string in OSGi-like format my.module.name;version=0.0.0 */
	public DefaultNameVersion(String nameVersion) {
		int index = nameVersion.indexOf(";version=");
		if (index < 0) {
			setName(nameVersion);
			setVersion(null);
		} else {
			setName(nameVersion.substring(0, index));
			setVersion(nameVersion.substring(index + ";version=".length()));
		}
	}

	public DefaultNameVersion(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public DefaultNameVersion(NameVersion nameVersion) {
		this.name = nameVersion.getName();
		this.version = nameVersion.getVersion();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NameVersion) {
			NameVersion nameVersion = (NameVersion) obj;
			return name.equals(nameVersion.getName())
					&& version.equals(nameVersion.getVersion());
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name + ":" + version;
	}

	public int compareTo(NameVersion o) {
		if (o.getName().equals(name))
			return version.compareTo(o.getVersion());
		else
			return name.compareTo(o.getName());
	}
}
