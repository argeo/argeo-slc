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
package org.argeo.slc.core.build;

import org.argeo.slc.NameVersion;
import org.springframework.core.io.Resource;

/**
 * The distribution of a software package (jar, zip, RPM, etc.) which is
 * versioned. The archive itself is accessible via a {@link Resource}.
 */
public class VersionedResourceDistribution extends ResourceDistribution
		implements NameVersion {
	private String name;
	private String version;

	public VersionedResourceDistribution() {
		super();
	}

	public VersionedResourceDistribution(NameVersion nameVersion,
			Resource resource) {
		this(nameVersion.getName(), nameVersion.getVersion(), resource);
	}

	public VersionedResourceDistribution(String name, String version,
			Resource resource) {
		super(resource);
		this.name = name;
		this.version = version;
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

}
