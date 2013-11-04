package org.argeo.slc.akb;

/**
 * SLC AKB's specific exception. For the time being, it just wraps a usual
 * RuntimeException
 */
public class AkbException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AkbException(String message) {
		super(message);
	}

	public AkbException(String message, Throwable e) {
		super(message, e);
	}

}
