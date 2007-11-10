package org.argeo.slc.core;

public class UnsupportedException extends SlcException {
	static final long serialVersionUID = 1l;

	public UnsupportedException(String message) {
		super(message);
	}

	public UnsupportedException(String nature, Object obj) {
		super("Unsupported " + nature + ": " + obj.getClass());
	}

	public UnsupportedException(String nature, String value) {
		super("Unsupported " + nature + ": " + value);
	}

}
