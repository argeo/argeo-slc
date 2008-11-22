package org.argeo.slc.runtime;

import java.util.Map;
import java.util.Properties;

import org.argeo.slc.runtime.SlcExecutionContext;
import org.argeo.slc.runtime.SlcExecutionOutput;

public interface SlcRuntime<T extends SlcExecutionContext> {
	public void executeScript(String runtime, String script, String targets,
			Properties properties, Map<String, Object> references,
			SlcExecutionOutput<T> executionOutput);
}
