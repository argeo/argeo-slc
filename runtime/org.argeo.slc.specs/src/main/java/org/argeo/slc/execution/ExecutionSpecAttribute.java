package org.argeo.slc.execution;

public interface ExecutionSpecAttribute {
	public Object getValue();

	public Boolean getIsParameter();

	public Boolean getIsFrozen();

	public Boolean getIsHidden();

}
