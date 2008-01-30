package org.argeo.slc.example;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.core.test.context.DefaultContextTestData;

public class ContextExampleTestDef extends TreeSRelatedHelper implements
		TestDefinition {

	public void execute(TestRun testRun) {
		DefaultContextTestData data = testRun.getTestData();
		ContextUtils
				.compareReachedExpected(data, testRun.getTestResult(), this);
	}

	public StructureElement getStructureElement(String key) {
		return new SimpleSElement(key);
	}

}
