package org.argeo.api.slc;

/** Basis for all SLC exceptions. This is an unchecked exception. */
public class SlcException extends RuntimeException {
	private static final long serialVersionUID = 6373738619304106445L;

	/** Constructor. */
	public SlcException(String message) {
		super(message);
	}

	/** Constructor. */
	public SlcException(String message, Throwable e) {
		super(message, e);
	}

}
