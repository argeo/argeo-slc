package org.argeo.slc.core;

/** Basis for all SLC exceptions. This is an unchecked exception. */
public class SlcException extends RuntimeException {
	static final long serialVersionUID = 1l;

	/** Constructor. */
	public SlcException(String message) {
		super(message);
	}

	/** Constructor. */
	public SlcException(String message, Throwable cause) {
		super(message, cause);
	}

}
