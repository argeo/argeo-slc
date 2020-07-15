package org.argeo.slc.repo.maven;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.aether.artifact.Artifact;

/**
 * Compare two artifacts, for use in {@link TreeSet} / {@link TreeMap}, consider
 * artifactId first THEN groupId
 */
public class ArtifactIdComparator implements Comparator<Artifact> {
	public int compare(Artifact o1, Artifact o2) {
		if (o1.getArtifactId().equals(o2.getArtifactId()))
			return o1.getGroupId().compareTo(o2.getGroupId());
		return o1.getArtifactId().compareTo(o2.getArtifactId());
	}

}
