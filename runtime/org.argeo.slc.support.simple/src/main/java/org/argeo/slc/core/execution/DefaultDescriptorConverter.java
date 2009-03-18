package org.argeo.slc.core.execution;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionFlowDescriptorConverter;

public class DefaultDescriptorConverter implements
		ExecutionFlowDescriptorConverter {

	public Map<String, Object> convertValues(
			ExecutionFlowDescriptor executionFlowDescriptor) {
		// convert the values of flow.getFlowDescriptor()
		Map<String, Object> values = executionFlowDescriptor.getValues();

		Map<String, Object> convertedValues = new HashMap<String, Object>();

		for (String key : values.keySet()) {
			Object value = values.get(key);
			if (value instanceof PrimitiveValue) {
				PrimitiveValue primitiveValue = (PrimitiveValue) value;

				// TODO: check that the class of the the primitiveValue.value
				// matches
				// the primitiveValue.type
				convertedValues.put(key, primitiveValue.getValue());
			} else if (value instanceof RefValue) {
				RefValue refValue = (RefValue) value;
				convertedValues.put(key, refValue.getLabel());
			}
		}
		return convertedValues;
	}

}
