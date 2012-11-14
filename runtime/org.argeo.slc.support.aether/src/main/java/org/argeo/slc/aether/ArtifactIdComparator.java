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

import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sonatype.aether.artifact.Artifact;

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
