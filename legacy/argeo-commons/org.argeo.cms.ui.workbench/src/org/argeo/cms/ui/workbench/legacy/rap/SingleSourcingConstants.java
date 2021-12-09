package org.argeo.cms.ui.workbench.legacy.rap;

/**
 * Centralise constants that are used in both RAP and RCP specific code to avoid
 * duplicated declaration
 */
public interface SingleSourcingConstants {

	// Single sourced open file command
	String OPEN_FILE_CMD_ID = "org.argeo.cms.ui.workbench.openFile";
	String PARAM_FILE_NAME = "param.fileName";
	String PARAM_FILE_URI = "param.fileURI";

	String SCHEME_HOST_SEPARATOR = "://";
	String FILE_SCHEME = "file";
	String JCR_SCHEME = "jcr";
}
