package org.argeo.slc.core.execution;

public class PrimitiveSpecAttribute extends AbstractSpecAttribute {
//	public enum Type {
//		string, integer
//	}

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

	public void setType(String type) {
		this.type = type;
	}

}
