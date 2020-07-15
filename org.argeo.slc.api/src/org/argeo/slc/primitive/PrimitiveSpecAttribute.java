package org.argeo.slc.primitive;

import org.argeo.slc.SlcException;
import org.argeo.slc.execution.AbstractSpecAttribute;

/**
 * A spec attribute wrapping a primitive value.
 * 
 * @see PrimitiveAccessor
 */
public class PrimitiveSpecAttribute extends AbstractSpecAttribute implements
		PrimitiveAccessor {
	private static final long serialVersionUID = -566676381839825483L;
	private String type = "string";
	private Object value = null;

	public PrimitiveSpecAttribute() {
	}

	public PrimitiveSpecAttribute(String type, Object value) {
		this.type = type;
		this.value = value;
	}

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
		// check whether type is recognized.
		if (PrimitiveUtils.typeAsClass(type) == null)
			throw new SlcException("Unrecognized type " + type);
		this.type = type;

	}

	@Override
	public String toString() {
		return "Primitive spec attribute [" + type + "]"
				+ (value != null ? "=" + value : "");
	}

}
