package org.argeo.slc.ant;

import org.argeo.slc.core.SlcException;

/** Base for all SLC Ant exceptions. */
public class SlcAntException extends SlcException {
	static final long serialVersionUID = 1l;

	public SlcAntException(String message) {
		super(message);
	}

	public SlcAntException(String message, Throwable cause) {
		super(message, cause);
	}

}
