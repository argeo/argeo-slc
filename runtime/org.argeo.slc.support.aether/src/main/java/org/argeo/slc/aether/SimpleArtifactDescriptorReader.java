package org.argeo.slc.aether;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;

public class SimpleArtifactDescriptorReader implements ArtifactDescriptorReader {

	public ArtifactDescriptorResult readArtifactDescriptor(
			RepositorySystemSession session, ArtifactDescriptorRequest request)
			throws ArtifactDescriptorException {
		ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);
		return result;
	}

}
