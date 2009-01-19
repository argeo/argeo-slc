package org.argeo.slc.runtime;

import java.util.Map;
import java.util.Properties;

import org.argeo.slc.process.SlcExecution;

public interface SlcApplication<T extends SlcExecutionContext> {
	public void execute(SlcExecution slcExecution, Properties properties,
			Map<String, Object> references,
			SlcExecutionOutput<T> executionOutput);
}
