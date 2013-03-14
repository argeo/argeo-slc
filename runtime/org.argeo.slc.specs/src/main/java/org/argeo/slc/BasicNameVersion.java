/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc;

import java.io.Serializable;

public class BasicNameVersion implements NameVersion, Comparable<NameVersion>,
		Serializable {
	private static final long serialVersionUID = -5127304279136195127L;
	private String name;
	private String version;

	public BasicNameVersion() {
	}

	/** Interprets string in OSGi-like format my.module.name;version=0.0.0 */
	public BasicNameVersion(String nameVersion) {
		int index = nameVersion.indexOf(";version=");
		if (index < 0) {
			name = nameVersion;
			version = null;
		} else {
			name = nameVersion.substring(0, index);
			version = nameVersion.substring(index + ";version=".length());
		}
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
