package org.argeo.slc.repo;

import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** A {@link Distribution} based on an Aether {@link Artifact} */
public class ArtifactDistribution extends DefaultNameVersion implements
		Distribution {
	private final Artifact artifact;

	public ArtifactDistribution(Artifact artifact) {
		this.artifact = artifact;
		setName(artifact.getArtifactId());
		setVersion(artifact.getVersion());
	}

	public ArtifactDistribution(String coords) {
		this(new DefaultArtifact(coords));
	}

	/** Aether coordinates of the underlying artifact. */
	public String getDistributionId() {
		return artifact.toString();
	}

	public Artifact getArtifact() {
		return artifact;
	}

	@Override
	public int hashCode() {
		return artifact.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NameVersion)
			return super.equals(obj);
		else
			return artifact.equals(obj);
	}

	@Override
	public String toString() {
		return getDistributionId();
	}

}
