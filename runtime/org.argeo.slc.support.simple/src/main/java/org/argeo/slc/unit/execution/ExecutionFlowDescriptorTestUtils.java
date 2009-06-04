package org.argeo.slc.unit.execution;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.core.deploy.SimpleExecutables;
import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.PrimitiveValue;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValue;
import org.argeo.slc.core.test.BasicTestData;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpecAttribute;

public class ExecutionFlowDescriptorTestUtils {
	public static ExecutionFlowDescriptor createSimpleExecutionFlowDescriptor() {
		ExecutionFlowDescriptor flowDescriptor = new ExecutionFlowDescriptor();
		flowDescriptor.setName("simpleFlow");
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("primitiveInteger", new PrimitiveValue(
				PrimitiveSpecAttribute.TYPE_INTEGER, 100));
		values.put("ref1", new RefValue("Just a label"));
		flowDescriptor.setValues(values);

		flowDescriptor.setExecutionSpec(createRelatedSimpleSpec());
		return flowDescriptor;
	}

	protected static DefaultExecutionSpec createRelatedSimpleSpec() {
		DefaultExecutionSpec spec = new DefaultExecutionSpec();
		spec.setBeanName("simpleSpec");
		Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

		PrimitiveSpecAttribute primitiveInteger = new PrimitiveSpecAttribute();
		primitiveInteger.setType(PrimitiveSpecAttribute.TYPE_INTEGER);
		primitiveInteger.setValue(50);
		attributes.put("primitiveInteger", primitiveInteger);

		RefSpecAttribute ref1 = new RefSpecAttribute();
		ref1.setTargetClass(BasicTestData.class);
		attributes.put("ref1", ref1);

		spec.setAttributes(attributes);

		return spec;
	}
}
