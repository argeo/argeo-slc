package org.argeo.slc.core.execution;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.argeo.slc.execution.ExecutionContext;

public class MapExecutionContext implements ExecutionContext {
	private final Map<String, Object> variables = Collections
			.synchronizedMap(new HashMap<String, Object>());

	private final String uuid;

	public MapExecutionContext() {
		uuid = UUID.randomUUID().toString();
		variables.put(VAR_EXECUTION_CONTEXT_ID, uuid);
		variables.put(VAR_EXECUTION_CONTEXT_CREATION_DATE, new Date());
	}

	public void setVariable(String key, Object value) {
		variables.put(key, value);
	}

	public Object getVariable(String key) {
		return variables.get(key);
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExecutionContext)
			return uuid.equals(((ExecutionContext) obj).getUuid());
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"#"+uuid;
	}

}
