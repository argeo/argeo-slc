package org.argeo.slc.core.execution;

import org.argeo.slc.SlcException;

public class PrimitiveSpecAttribute extends AbstractSpecAttribute implements
		PrimitiveAccessor {
	// public enum Type {
	// string, integer
	// }

	public final static String TYPE_STRING = "string";
	public final static String TYPE_INTEGER = "integer";
	public final static String TYPE_LONG = "long";
	public final static String TYPE_FLOAT = "float";
	public final static String TYPE_DOUBLE = "double";
	public final static String TYPE_BOOLEAN = "boolean";

	private String type = "string";
	private Object value = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public Class<?> getTypeAsClass() {
		return typeAsClass(type);
	}

	public void setType(String type) {
		this.type = type;

		// check whether type is recognized.
		// TODO: make validation cleaner
		typeAsClass(type);
	}

	public static Class<?> typeAsClass(String type) {
		if (TYPE_STRING.equals(type))
			return String.class;
		else if (TYPE_INTEGER.equals(type))
			return Integer.class;
		else if (TYPE_LONG.equals(type))
			return Long.class;
		else if (TYPE_FLOAT.equals(type))
			return Float.class;
		else if (TYPE_DOUBLE.equals(type))
			return Double.class;
		else if (TYPE_BOOLEAN.equals(type))
			return Boolean.class;
		else
			throw new SlcException("Unrecognized type " + type);
	}

}
