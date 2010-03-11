package org.argeo.slc.castor.execution;

import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class PrimitiveFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		if (object == null)
			return null;

		Object value = ((PrimitiveAccessor) object).getValue();
		return value != null ? value.toString() : null;
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
		primitiveAccessor.setValue(PrimitiveUtils.convert(type, str));
	}

}
