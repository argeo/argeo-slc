package org.argeo.slc.deploy;

import java.io.Serializable;

import org.argeo.slc.build.BasicNameVersion;

public class ModuleDescriptor extends BasicNameVersion implements Serializable {
	private static final long serialVersionUID = 1L;
	private String label;
	private String description;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
