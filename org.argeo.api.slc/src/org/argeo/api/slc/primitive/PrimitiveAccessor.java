package org.argeo.api.slc.primitive;

/** Abstraction of access to primitive values */
public interface PrimitiveAccessor {
	public final static String TYPE_STRING = "string";
	/**
	 * As of Argeo 1, passwords are NOT stored encrypted, just hidden in the UI,
	 * but stored in plain text in JCR. Use keyring instead.
	 */
	public final static String TYPE_PASSWORD = "password";
	public final static String TYPE_INTEGER = "integer";
	public final static String TYPE_LONG = "long";
	public final static String TYPE_FLOAT = "float";
	public final static String TYPE_DOUBLE = "double";
	public final static String TYPE_BOOLEAN = "boolean";

	public String getType();

	public Object getValue();

	public void setValue(Object value);
}
