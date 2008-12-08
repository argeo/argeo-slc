package org.argeo.slc.maven.plugins.pde;

import java.util.List;

public class Feature {
	private String updateSite;
	private String copyright;
	private List plugins;

	public String getUpdateSite() {
		return updateSite;
	}

	public void setUpdateSite(String license) {
		this.updateSite = license;
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
