package org.argeo.slc.primitive;

/** Converts to and from primitive types. */
public class PrimitiveUtils {
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_STRING} instead
	 */
	public final static String TYPE_STRING = PrimitiveAccessor.TYPE_STRING;
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_INTEGER} instead
	 */
	public final static String TYPE_INTEGER = PrimitiveAccessor.TYPE_INTEGER;
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_LONG} instead
	 */
	public final static String TYPE_LONG = PrimitiveAccessor.TYPE_LONG;
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_FLOAT} instead
	 */
	public final static String TYPE_FLOAT = PrimitiveAccessor.TYPE_FLOAT;
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_DOUBLE} instead
	 */
	public final static String TYPE_DOUBLE = PrimitiveAccessor.TYPE_DOUBLE;
	/**
	 * @deprecated Use {@link PrimitiveAccessor#TYPE_BOOLEAN} instead
	 */
	public final static String TYPE_BOOLEAN = PrimitiveAccessor.TYPE_BOOLEAN;

	private PrimitiveUtils() {

	}

	/** @return the class or null if the provided type is not a primitive */
	public static Class<?> typeAsClass(String type) {
		if (PrimitiveAccessor.TYPE_STRING.equals(type))
			return String.class;
		else if (PrimitiveAccessor.TYPE_PASSWORD.equals(type))
			return char[].class;
		else if (PrimitiveAccessor.TYPE_INTEGER.equals(type))
			return Integer.class;
		else if (PrimitiveAccessor.TYPE_LONG.equals(type))
			return Long.class;
		else if (PrimitiveAccessor.TYPE_FLOAT.equals(type))
			return Float.class;
		else if (PrimitiveAccessor.TYPE_DOUBLE.equals(type))
			return Double.class;
		else if (PrimitiveAccessor.TYPE_BOOLEAN.equals(type))
			return Boolean.class;
		else
			return null;
	}

	/** @return the type or null if the provided class is not a primitive */
	public static String classAsType(Class<?> clss) {
		if (String.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_STRING;
		else if (char[].class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_PASSWORD;
		else if (Integer.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_INTEGER;
		else if (Long.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_LONG;
		else if (Float.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_FLOAT;
		else if (Double.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_DOUBLE;
		else if (Boolean.class.isAssignableFrom(clss))
			return PrimitiveAccessor.TYPE_BOOLEAN;
		else
			return null;
	}

	/** Parse string as an object. Passwords are returned as String.*/
	public static Object convert(String type, String str) {
		if (PrimitiveAccessor.TYPE_STRING.equals(type)) {
			return str;
		} else if (PrimitiveAccessor.TYPE_PASSWORD.equals(type)) {
			return str;
		} else if (PrimitiveAccessor.TYPE_INTEGER.equals(type)) {
			return (Integer.parseInt(str));
		} else if (PrimitiveAccessor.TYPE_LONG.equals(type)) {
			return (Long.parseLong(str));
		} else if (PrimitiveAccessor.TYPE_FLOAT.equals(type)) {
			return (Float.parseFloat(str));
		} else if (PrimitiveAccessor.TYPE_DOUBLE.equals(type)) {
			return (Double.parseDouble(str));
		} else if (PrimitiveAccessor.TYPE_BOOLEAN.equals(type)) {
			return (Boolean.parseBoolean(str));
		} else {
			return str;
		}
	}

}
