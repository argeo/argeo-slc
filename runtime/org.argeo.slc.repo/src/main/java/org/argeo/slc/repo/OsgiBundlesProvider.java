package org.argeo.slc.repo;

import java.util.List;

/**
 * Provides OSGi bundles either by linking to them, by wrapping existing
 * archives or by building them.
 */
public interface OsgiBundlesProvider {
	/** The provided bundles in the order they will be retrieved/wrapped/built. */
	public List<ArtifactDistribution> provides();
}
