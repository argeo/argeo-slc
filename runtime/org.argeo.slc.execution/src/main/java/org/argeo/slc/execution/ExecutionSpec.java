package org.argeo.slc.execution;

import java.util.Map;

public interface ExecutionSpec {
	public Map<String, ExecutionSpecAttribute> getAttributes();

	public String getName();
}
