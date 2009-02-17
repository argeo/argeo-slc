package org.argeo.slc.executionflow;

import java.util.Map;

public interface ExecutionSpec {
	public Map<String, ExecutionSpecAttribute> getAttributes();
}
