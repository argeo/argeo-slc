package org.argeo.slc.msg.build;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.build.BasicNameVersion;

public class ModularDistributionDescriptor extends BasicNameVersion implements
		Serializable {
	private static final long serialVersionUID = 1L;

	/** key is type, value the URL */
	private Map<String, String> modulesDescriptors = new HashMap<String, String>();

	public Map<String, String> getModulesDescriptors() {
		return modulesDescriptors;
	}

	public void setModulesDescriptors(Map<String, String> urls) {
		this.modulesDescriptors = urls;
	}

}
