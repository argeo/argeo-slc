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
	public final static String SLC_LOG = "slc:log";
	public final static String SLC_TIMESTAMP = "slc:timestamp";

	// spec attribute
	public final static String SLC_IS_IMMUTABLE = "slc:isImmutable";
	public final static String SLC_IS_CONSTANT = "slc:isConstant";
	public final static String SLC_IS_HIDDEN = "slc:isHidden";

	// result
	public final static String SLC_SUCCESS = "slc:success";
	public final static String SLC_MESSAGE = "slc:message";
	public final static String SLC_TAG = "slc:tag";
	public final static String SLC_ERROR_MESSAGE = "slc:errorMessage";
	public final static String SLC_TEST_CASE = "slc:testCase";
	public final static String SLC_TEST_CASE_TYPE = "slc:testCaseType";

	// diff result
	public final static String SLC_SUMMARY = "slc:summary";
	public final static String SLC_ISSUES = "slc:issues";
	public final static String SLC_TOLERANCE = "slc:tolerance";
	public final static String SLC_RELATIVE_TOLERANCE = "slc:relativeTolerance";
	public final static String SLC_KEY_COLUMN = "slc:keyColumn";
	public final static String SLC_MISMATCH = "slc:mismatch";
	public final static String SLC_LEFT_OVER = "slc:leftOver";
	public final static String SLC_MISSING = "slc:missing";
	public final static String SLC_LEFT_OVER_MAX = "slc:leftOverMax";
	public final static String SLC_MISSING_MAX = "slc:missingMax";
	public final static String SLC_DETAILED = "slc:detailed";
	public final static String SLC_ZERO_MISMATCH = "slc:zeroMismatch";

	/*
	 * REPO
	 */
	// shared
	public final static String SLC_URL = "slc:url";
	public final static String SLC_OPTIONAL = "slc:optional";
	public final static String SLC_AS_STRING = "slc:asString";

	// origin
	public final static String SLC_ORIGIN = "slc:origin";
	public final static String SLC_PROXY = "slc:proxy";

	// slc:artifact
	public final static String SLC_ARTIFACT_ID = "slc:artifactId";
	public final static String SLC_GROUP_ID = "slc:groupId";
	public final static String SLC_GROUP_BASE_ID = "slc:groupBaseId";
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
