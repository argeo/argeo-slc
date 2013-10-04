package org.argeo.slc.repo;

import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.build.Distribution;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** A {@link Distribution} based on an Aether {@link Artifact} */
public class ArtifactDistribution implements Distribution,
		CategorizedNameVersion {
	private final Artifact artifact;

	public ArtifactDistribution(Artifact artifact) {
		this.artifact = artifact;
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

	public String getName() {
		return getArtifact().getArtifactId();
	}

	public String getVersion() {
		return getArtifact().getVersion();
	}

	public String getCategory() {
		return getArtifact().getGroupId();
	}

	@Override
	public int hashCode() {
		return artifact.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CategorizedNameVersion) {
			CategorizedNameVersion cnv = (CategorizedNameVersion) obj;
			return getCategory().equals(cnv.getCategory())
					&& getName().equals(cnv.getName())
					&& getVersion().equals(cnv.getVersion());
		} else
			return artifact.equals(obj);
	}

	@Override
	public String toString() {
		return getDistributionId();
	}

}
