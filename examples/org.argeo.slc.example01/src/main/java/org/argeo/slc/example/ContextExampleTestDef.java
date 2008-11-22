package org.argeo.slc.example;

import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.context.ContextUtils;
import org.argeo.slc.core.test.context.DefaultContextTestData;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestRun;

public class ContextExampleTestDef extends TreeSRelatedHelper implements
		TestDefinition {

	public void execute(TestRun testRun) {
		DefaultContextTestData data = testRun.getTestData();
		ContextUtils
				.compareReachedExpected(data, testRun.getTestResult(), this);
	}
}
