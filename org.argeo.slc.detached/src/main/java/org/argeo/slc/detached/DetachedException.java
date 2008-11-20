package org.argeo.slc.detached;

public class DetachedException extends RuntimeException {
	static final long serialVersionUID = 1l;

	public DetachedException(String message) {
		super(message);
	}

	public DetachedException(String message, Exception cause) {
		super(message, cause);
	}
}
