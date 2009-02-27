package org.argeo.slc.castor.execution;

import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.PrimitiveValue;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class PrimitiveFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		if (object == null)
			return null;

		Object value = null;
		if (object instanceof PrimitiveSpecAttribute)
			value = ((PrimitiveSpecAttribute) object).getValue();
		else if (object instanceof PrimitiveValue)
			value = ((PrimitiveValue) object).getValue();
		else
			throw new IllegalStateException("Unkown type " + object.getClass());

		return value != null ? value.toString() : null;
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		// TODO: could probably be more generic

		PrimitiveSpecAttribute attr = (PrimitiveSpecAttribute) object;
		String type = attr.getType();
		String str = value.toString();

		// FIXME: generalize
		if (object instanceof PrimitiveSpecAttribute)
			((PrimitiveSpecAttribute) object).setValue(convert(type, str));
		else if (object instanceof PrimitiveValue)
			((PrimitiveValue) object).setValue(convert(type, str));
		else
			throw new IllegalStateException("Unkown type " + object.getClass());
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
