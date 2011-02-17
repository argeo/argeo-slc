package org.argeo.slc.repo;

/** Names used for items (nodes and properties). */
public interface RepoNames {
	public final static String SLC_ = "slc:";

	// shared
	public final static String SLC_NAME = "slc:name";
	public final static String SLC_VERSION = "slc:version";
	public final static String SLC_OPTIONAL = "slc:optional";

	// slc:artifact
	public final static String SLC_ARTIFACT_ID = "slc:artifactId";
	public final static String SLC_GROUP_ID = "slc:groupId";
	public final static String SLC_ARTIFACT_VERSION = "slc:artifactVersion";
	public final static String SLC_ARTIFACT_EXTENSION = "slc:artifactExtension";
	public final static String SLC_ARTIFACT_CLASSIFIER = "slc:artifactClassifier";

	// slc:jarArtifact
	public final static String SLC_MANIFEST = "slc:manifest";

	// shared OSGi
	public final static String SLC_SYMBOLIC_NAME = "slc:symbolicName";
	public final static String SLC_BUNDLE_VERSION = "slc:bundle-version";

	// slc:osgiBaseVersion
	public final static String SLC_MAJOR = "slc:major";
	public final static String SLC_MINOR = "slc:minor";
	public final static String SLC_MICRO = "slc:micro";
	// slc:osgiVersion
	public final static String SLC_QUALIFIER = "slc:qualifier";

	// slc:exportedPackage
	public final static String SLC_USES = "slc:uses";
}
