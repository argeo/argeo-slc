package org.argeo.slc.jcr;

import org.argeo.slc.SlcNames;

/** JCR related constants used across SLC */
public interface SlcJcrConstants {
	public final static String PROPERTY_PATH = "argeo.slc.jcr.path";

	public final static String SLC_BASE_PATH = "/" + SlcNames.SLC_SYSTEM;
	public final static String AGENTS_BASE_PATH = SLC_BASE_PATH + "/"
			+ SlcNames.SLC_AGENTS;
	public final static String VM_AGENT_FACTORY_PATH = AGENTS_BASE_PATH + "/"
			+ SlcNames.SLC_VM;
}
