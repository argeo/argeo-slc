package org.argeo.slc.castor.execution;

import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class PrimitiveFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		if (object == null)
			return null;

		return ((PrimitiveAccessor) object).getValue().toString();
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		return null;
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		PrimitiveAccessor primitiveAccessor = (PrimitiveAccessor) object;
		String type = primitiveAccessor.getType();
		String str = value.toString();
		primitiveAccessor.setValue(convert(type, str));
	}

	protected Object convert(String type, String str) {
		if (PrimitiveSpecAttribute.TYPE_STRING.equals(type)) {
			return str;
		} else if (PrimitiveSpecAttribute.TYPE_INTEGER.equals(type)) {
			return (Integer.parseInt(str));
		} else if (PrimitiveSpecAttribute.TYPE_LONG.equals(type)) {
			return (Long.parseLong(str));
		} else if (PrimitiveSpecAttribute.TYPE_FLOAT.equals(type)) {
			return (Float.parseFloat(str));
		} else if (PrimitiveSpecAttribute.TYPE_DOUBLE.equals(type)) {
			return (Double.parseDouble(str));
		} else if (PrimitiveSpecAttribute.TYPE_BOOLEAN.equals(type)) {
			return (Boolean.parseBoolean(str));
		} else {
			return str;
		}
	}
}
