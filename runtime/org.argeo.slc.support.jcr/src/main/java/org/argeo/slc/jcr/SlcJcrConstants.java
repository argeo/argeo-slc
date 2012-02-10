package org.argeo.slc.jcr;

/** JCR related constants used across SLC */
public interface SlcJcrConstants {
	public final static String PROPERTY_PATH = "argeo.slc.jcr.path";
	public final static String SLC_BASE_PATH = "/slc:system";
	public final static String PROCESSES_BASE_PATH = SLC_BASE_PATH
			+ "/slc:processes";
	public final static String AGENTS_BASE_PATH = SLC_BASE_PATH + "/slc:agents";
	public final static String RESULTS_BASE_PATH = SLC_BASE_PATH
			+ "/slc:results";
	public final static String VM_AGENT_FACTORY_PATH = AGENTS_BASE_PATH
			+ "/slc:vm";
}
