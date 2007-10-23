package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;

public class SlcTestTask extends AbstractSpringTask {

	private TestDefinitionArg testDefinitionArg;
	private TestDataArg testDataArg;

	@Override
	public void execute() throws BuildException {
		TestDefinition testDefinition = testDefinitionArg.getTestDefinition();
		testDefinition.setTestData(testDataArg.getTestData());
		testDefinition.execute();
	}

	public TestDefinitionArg createTestDefinition() {
		testDefinitionArg = new TestDefinitionArg();
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		return testDataArg;
	}
}

class TestDefinitionArg extends AbstractSpringArg {
	public TestDefinition getTestDefinition(){
		return (TestDefinition)getBeanInstance();
	}
}

class TestDataArg extends AbstractSpringArg {
	public TestData getTestData(){
		return (TestData)getBeanInstance();
	}

}
