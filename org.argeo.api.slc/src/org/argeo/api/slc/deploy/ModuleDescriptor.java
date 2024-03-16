package org.argeo.api.slc.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.api.slc.DefaultNameVersion;

/** The description of a versioned module. */
public class ModuleDescriptor extends DefaultNameVersion implements Serializable {
	private static final long serialVersionUID = 4310820315478645419L;
	private String title;
	private String description;
	private Map<String, String> metadata = new HashMap<String, String>();
	private Boolean started = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/** @deprecated use {@link #getTitle()} instead */
	public String getLabel() {
		return title;
	}

	/** @deprecated use {@link #setTitle(String)} instead */
	public void setLabel(String label) {
		this.title = label;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Boolean getStarted() {
		return started;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}

}
