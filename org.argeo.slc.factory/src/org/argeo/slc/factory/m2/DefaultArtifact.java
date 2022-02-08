package org.argeo.slc.factory.m2;

import org.argeo.slc.DefaultCategoryNameVersion;

/**
 * Simple representation of an M2 artifact, not taking into account classifiers,
 * types, etc.
 */
public class DefaultArtifact extends DefaultCategoryNameVersion implements Artifact {
	private String classifier;

	public DefaultArtifact(String m2coordinates) {
		this(m2coordinates, null);
	}

	public DefaultArtifact(String m2coordinates, String classifier) {
		String[] parts = m2coordinates.split(":");
		setCategory(parts[0]);
		setName(parts[1]);
		if (parts.length > 2) {
			setVersion(parts[2]);
		}
		this.classifier = classifier;
	}

	@Override
	public String getGroupId() {
		return getCategory();
	}

	@Override
	public String getArtifactId() {
		return getName();
	}

	public String toM2Coordinates() {
		return getCategory() + ":" + getName() + (getVersion() != null ? ":" + getVersion() : "");
	}

	public String getClassifier() {
		return classifier != null ? classifier : "";
	}

}
