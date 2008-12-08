package org.argeo.slc.maven.plugins.pde;

public class Plugin {
	private String id;
	private String version;
	private String unpack = "false";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUnpack() {
		return unpack;
	}

	public void setUnpack(String unpack) {
		this.unpack = unpack;
	}

}
