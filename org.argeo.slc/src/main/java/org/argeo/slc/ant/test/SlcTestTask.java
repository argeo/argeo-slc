package org.argeo.slc.ant.test;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.SAwareArg;
import org.argeo.slc.ant.SAwareTask;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;

public class SlcTestTask extends SAwareTask {

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
		testDefinitionArg.setParentSAware(sAware);
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		testDataArg.setParentSAware(sAware);
		return testDataArg;
	}
}

class TestDefinitionArg extends SAwareArg {
	public TestDefinition getTestDefinition(){
		return (TestDefinition)getBeanInstance();
	}
}

class TestDataArg extends SAwareArg {
	public TestData getTestData(){
		return (TestData)getBeanInstance();
	}

}
