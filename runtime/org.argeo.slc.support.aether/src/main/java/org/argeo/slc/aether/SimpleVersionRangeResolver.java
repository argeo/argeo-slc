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
package org.argeo.slc.aether;

import org.argeo.slc.aether.osgi.OsgiVersion;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResolutionException;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.version.Version;

public class SimpleVersionRangeResolver implements VersionRangeResolver {

	public VersionRangeResult resolveVersionRange(
			RepositorySystemSession session, VersionRangeRequest request)
			throws VersionRangeResolutionException {
		VersionRangeResult versionRangeResult = new VersionRangeResult(request);
		Version version = new OsgiVersion(request.getArtifact()
				.getBaseVersion());
		versionRangeResult.addVersion(version);
		return null;
	}
}
