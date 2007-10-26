package org.argeo.slc.core;

/** Basis for all SLC exceptions.*/
public class SlcException extends RuntimeException {
	static final long serialVersionUID = 1l;

	public SlcException(String message) {
		super(message);
	}

	public SlcException(String message, Throwable cause) {
		super(message, cause);
	}

}
