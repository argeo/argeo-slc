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
