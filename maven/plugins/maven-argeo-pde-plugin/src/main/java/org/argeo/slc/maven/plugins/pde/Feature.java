package org.argeo.slc.maven.plugins.pde;

import java.util.List;

public class Feature {
	private String providerName;
	private String license;
	private String copyright;
	private List plugins;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public List getPlugins() {
		return plugins;
	}

	public void setPlugins(List plugins) {
		this.plugins = plugins;
	}

}
