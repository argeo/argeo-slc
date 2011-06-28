package org.argeo.slc.jcr;

/** JCR names used by SLC */
public interface SlcNames {
	public final static String SLC_ = "slc:";

	public final static String SLC_UUID = "slc:uuid";
	public final static String SLC_STATUS = "slc:status";
	public final static String SLC_TYPE = "slc:type";
	public final static String SLC_NAME = "slc:name";
	public final static String SLC_VERSION = "slc:version";
	public final static String SLC_VALUE = "slc:value";
	public final static String SLC_ADDRESS = "slc:address";

	public final static String SLC_STARTED = "slc:started";
	public final static String SLC_COMPLETED = "slc:completed";

	public final static String SLC_SPEC = "slc:spec";
	public final static String SLC_EXECUTION_SPECS = "slc:executionSpecs";
	public final static String SLC_FLOW = "slc:flow";

	// spec attribute
	public final static String SLC_IS_IMMUTABLE = "slc:isImmutable";
	public final static String SLC_IS_CONSTANT = "slc:isConstant";
	public final static String SLC_IS_HIDDEN = "slc:isHidden";

	// result
	public final static String SLC_SUCCESS = "slc:success";
	public final static String SLC_MESSAGE = "slc:message";
	public final static String SLC_TAG = "slc:tag";
	public final static String SLC_ERROR_MESSAGE = "slc:errorMessage";

	/*
	 * REPO
	 */
	// shared
	public final static String SLC_URL = "slc:url";
	public final static String SLC_OPTIONAL = "slc:optional";
	public final static String SLC_AS_STRING = "slc:asString";

	// slc:artifact
	public final static String SLC_ARTIFACT_ID = "slc:artifactId";
	public final static String SLC_GROUP_ID = "slc:groupId";
	public final static String SLC_ARTIFACT_VERSION = "slc:artifactVersion";
	public final static String SLC_ARTIFACT_EXTENSION = "slc:artifactExtension";
	public final static String SLC_ARTIFACT_CLASSIFIER = "slc:artifactClassifier";

	// slc:jarArtifact
	public final static String SLC_MANIFEST = "slc:manifest";

	// shared OSGi
	public final static String SLC_SYMBOLIC_NAME = "slc:symbolic-name";
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