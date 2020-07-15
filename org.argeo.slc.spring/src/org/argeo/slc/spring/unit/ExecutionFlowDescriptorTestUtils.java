package org.argeo.slc.spring.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.RefSpecAttribute;
import org.argeo.slc.execution.RefValue;
import org.argeo.slc.execution.RefValueChoice;
import org.argeo.slc.primitive.PrimitiveAccessor;
import org.argeo.slc.primitive.PrimitiveSpecAttribute;
import org.argeo.slc.primitive.PrimitiveValue;
import org.argeo.slc.runtime.test.BasicTestData;

public class ExecutionFlowDescriptorTestUtils {
	public static ExecutionFlowDescriptor createSimpleExecutionFlowDescriptor() {
		ExecutionFlowDescriptor flowDescriptor = new ExecutionFlowDescriptor();
		flowDescriptor.setName("simpleFlow");
		flowDescriptor.setDescription("my description");

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("primitiveInteger", new PrimitiveValue(
				PrimitiveAccessor.TYPE_INTEGER, 100));

		RefValue refValue = new RefValue("002");
		values.put("ref1", refValue);
		flowDescriptor.setValues(values);

		flowDescriptor.setExecutionSpec(createRelatedSimpleSpec());
		return flowDescriptor;
	}

	protected static DefaultExecutionSpec createRelatedSimpleSpec() {
		DefaultExecutionSpec spec = new DefaultExecutionSpec();
		spec.setBeanName("simpleSpec");
		Map<String, ExecutionSpecAttribute> attributes = new HashMap<String, ExecutionSpecAttribute>();

		PrimitiveSpecAttribute primitiveInteger = new PrimitiveSpecAttribute();
		primitiveInteger.setType(PrimitiveAccessor.TYPE_INTEGER);
		primitiveInteger.setValue(50);
		attributes.put("primitiveInteger", primitiveInteger);

		RefSpecAttribute ref1 = new RefSpecAttribute();
		ref1.setTargetClass(BasicTestData.class);
		ref1.setChoices(new ArrayList<RefValueChoice>());
		ref1.getChoices().add(new RefValueChoice("001", "desc"));
		ref1.getChoices().add(new RefValueChoice("002", null));
		ref1.getChoices().add(new RefValueChoice("003", null));
		attributes.put("ref1", ref1);

		spec.setAttributes(attributes);

		return spec;
	}
}
