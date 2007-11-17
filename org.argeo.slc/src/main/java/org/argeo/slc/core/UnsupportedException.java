package org.argeo.slc.core;

/** Exception for unsupported features or actions. */
public class UnsupportedException extends SlcException {
	static final long serialVersionUID = 1l;

	/** Action not supported. */
	public UnsupportedException() {
		this("Action not supported");
	}

	/** Constructor with a message. */
	public UnsupportedException(String message) {
		super(message);
	}

	/**
	 * Constructor generating a message.
	 * 
	 * @param nature
	 *            the nature of the unsupported object
	 * @param obj
	 *            the object itself (its class name will be used in message)
	 */
	public UnsupportedException(String nature, Object obj) {
		super("Unsupported " + nature + ": " + obj.getClass());
	}

	/**
	 * Constructor generating a message.
	 * 
	 * @param nature
	 *            the nature of the unsupported object
	 * @param value
	 *            the problematic value itself
	 */
	public UnsupportedException(String nature, String value) {
		super("Unsupported " + nature + ": " + value);
	}

}
