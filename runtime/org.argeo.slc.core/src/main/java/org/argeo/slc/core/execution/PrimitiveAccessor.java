package org.argeo.slc.core.execution;

public interface PrimitiveAccessor {
	public String getType();

	public Object getValue();

	public void setValue(Object value);
}
