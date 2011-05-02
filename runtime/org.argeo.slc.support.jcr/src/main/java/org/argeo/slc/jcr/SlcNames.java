package org.argeo.slc.jcr;

/** JCR names used by SLC */
public interface SlcNames {

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

}
