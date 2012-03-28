/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.BasicNameVersion;

/** The description of a versioned module. */
public class ModuleDescriptor extends BasicNameVersion implements Serializable {
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
