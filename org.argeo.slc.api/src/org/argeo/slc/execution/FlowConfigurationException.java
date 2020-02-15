package org.argeo.slc.execution;

import org.argeo.slc.SlcException;

/** The stack trace of such exceptions does not need to be displayed */
public class FlowConfigurationException extends SlcException {
	private static final long serialVersionUID = 8456260596346797321L;

	public FlowConfigurationException(String message) {
		super(message);
	}
}
