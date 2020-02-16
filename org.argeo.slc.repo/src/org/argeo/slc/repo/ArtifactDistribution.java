package org.argeo.slc.repo;

import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.build.Distribution;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/** A {@link Distribution} based on an Aether {@link Artifact} */
public class ArtifactDistribution implements Distribution,
		CategoryNameVersion {
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
		if (obj instanceof CategoryNameVersion) {
			CategoryNameVersion cnv = (CategoryNameVersion) obj;
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
