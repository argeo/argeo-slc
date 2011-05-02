package org.argeo.slc.jcr;

/** JCR node types used by SLC */
public interface SlcTypes {

	public final static String SLC_AGENT_FACTORY = "slc:agentFactory";
	public final static String SLC_AGENT = "slc:agent";
	public final static String SLC_MODULE = "slc:module";
	public final static String SLC_EXECUTION_MODULE = "slc:executionModule";
	public final static String SLC_EXECUTION_SPEC = "slc:executionSpec";
	public final static String SLC_EXECUTION_FLOW = "slc:executionFlow";
	public final static String SLC_PROCESS = "slc:process";
	public final static String SLC_REALIZED_FLOW = "slc:realizedFlow";

	public final static String SLC_EXECUTION_SPEC_ATTRIBUTE = "slc:executionSpecAttribute";
	public final static String SLC_PRIMITIVE_SPEC_ATTRIBUTE = "slc:primitiveSpecAttribute";
	public final static String SLC_REF_SPEC_ATTRIBUTE = "slc:refSpecAttribute";

	public final static String SLC_RESULT = "slc:result";
	public final static String SLC_CHECK = "slc:check";
	public final static String SLC_PROPERTY = "slc:property";

}
