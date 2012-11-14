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
package org.argeo.slc.aether;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.resolution.VersionRequest;
import org.sonatype.aether.resolution.VersionResolutionException;
import org.sonatype.aether.resolution.VersionResult;

public class SimpleVersionResolver implements VersionResolver {

	public VersionResult resolveVersion(RepositorySystemSession session,
			VersionRequest request) throws VersionResolutionException {
		VersionResult versionResult = new VersionResult(request);
		versionResult.setVersion(request.getArtifact().getBaseVersion());
		return versionResult;
	}

}
