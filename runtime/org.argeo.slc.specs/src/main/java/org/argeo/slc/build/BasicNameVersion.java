package org.argeo.slc.build;

public class BasicNameVersion implements NameVersion, Comparable<NameVersion> {
	private String name;
	private String version;

	public BasicNameVersion() {
	}

	public BasicNameVersion(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public BasicNameVersion(NameVersion nameVersion) {
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
		return name.hashCode() + version.hashCode();
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
