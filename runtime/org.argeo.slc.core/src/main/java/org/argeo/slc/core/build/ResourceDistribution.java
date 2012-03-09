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
package org.argeo.slc.core.build;

import java.io.IOException;
import java.io.InputStream;

import org.argeo.slc.SlcException;
import org.argeo.slc.StreamReadable;
import org.argeo.slc.build.Distribution;
import org.springframework.core.io.Resource;

/** A software distribution archive accessible via a {@link Resource}. */
public class ResourceDistribution implements Distribution, StreamReadable {
	private Resource resource;

	public ResourceDistribution() {
	}

	public ResourceDistribution(Resource location) {
		this.resource = location;
	}

	public String getDistributionId() {
		return resource.toString();
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public InputStream getInputStream() {
		try {
			return resource.getInputStream();
		} catch (IOException e) {
			throw new SlcException("Cannot get input stream", e);
		}
	}

	@Override
	public String toString() {
		return resource.toString();
	}

}
