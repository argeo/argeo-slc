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
