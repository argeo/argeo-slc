package org.argeo.slc.detached;

public class DetachedException extends RuntimeException {
	private Exception cause;

	public DetachedException(String message) {
		super(message);
	}

	public DetachedException(String message, Exception cause) {
		super(message);
		this.cause = cause;
	}
}
