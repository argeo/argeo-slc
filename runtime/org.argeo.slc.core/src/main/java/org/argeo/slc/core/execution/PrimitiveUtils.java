package org.argeo.slc.core.execution;


public class PrimitiveUtils {
	public final static String TYPE_STRING = "string";
	public final static String TYPE_INTEGER = "integer";
	public final static String TYPE_LONG = "long";
	public final static String TYPE_FLOAT = "float";
	public final static String TYPE_DOUBLE = "double";
	public final static String TYPE_BOOLEAN = "boolean";

	private PrimitiveUtils() {

	}

	/** @return the class or null if the provided type is not a primitive */
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
			return null;
	}

	/** @return the type or null if the provided class is not a primitive */
	public static String classAsType(Class<?> clss) {
		if (String.class.isAssignableFrom(clss))
			return TYPE_STRING;
		else if (Integer.class.isAssignableFrom(clss))
			return TYPE_INTEGER;
		else if (Long.class.isAssignableFrom(clss))
			return TYPE_LONG;
		else if (Float.class.isAssignableFrom(clss))
			return TYPE_FLOAT;
		else if (Double.class.isAssignableFrom(clss))
			return TYPE_DOUBLE;
		else if (Boolean.class.isAssignableFrom(clss))
			return TYPE_BOOLEAN;
		else
			return null;
	}

	public static Object convert(String type, String str) {
		if (TYPE_STRING.equals(type)) {
			return str;
		} else if (TYPE_INTEGER.equals(type)) {
			return (Integer.parseInt(str));
		} else if (TYPE_LONG.equals(type)) {
			return (Long.parseLong(str));
		} else if (TYPE_FLOAT.equals(type)) {
			return (Float.parseFloat(str));
		} else if (TYPE_DOUBLE.equals(type)) {
			return (Double.parseDouble(str));
		} else if (TYPE_BOOLEAN.equals(type)) {
			return (Boolean.parseBoolean(str));
		} else {
			return str;
		}
	}

}
