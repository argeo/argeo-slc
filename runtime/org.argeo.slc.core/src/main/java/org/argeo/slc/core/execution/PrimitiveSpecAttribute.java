package org.argeo.slc.core.execution;

import org.argeo.slc.SlcException;

public class PrimitiveSpecAttribute extends AbstractSpecAttribute implements
		PrimitiveAccessor {
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
		// check whether type is recognized.
		if (PrimitiveUtils.typeAsClass(type) == null)
			throw new SlcException("Unrecognized type " + type);
		this.type = type;

	}
}
