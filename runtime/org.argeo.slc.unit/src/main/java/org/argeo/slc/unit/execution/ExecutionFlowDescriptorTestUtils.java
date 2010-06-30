/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.unit.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.core.execution.DefaultExecutionSpec;
import org.argeo.slc.core.execution.PrimitiveSpecAttribute;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.core.execution.PrimitiveValue;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValue;
import org.argeo.slc.core.execution.RefValueChoice;
import org.argeo.slc.core.test.BasicTestData;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionSpecAttribute;

public class ExecutionFlowDescriptorTestUtils {
	public static ExecutionFlowDescriptor createSimpleExecutionFlowDescriptor() {
		ExecutionFlowDescriptor flowDescriptor = new ExecutionFlowDescriptor();
		flowDescriptor.setName("simpleFlow");
		flowDescriptor.setDescription("my description");

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("primitiveInteger", new PrimitiveValue(
				PrimitiveUtils.TYPE_INTEGER, 100));

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
		primitiveInteger.setType(PrimitiveUtils.TYPE_INTEGER);
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
