package org.argeo.slc.ant.test;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.structure.SAwareArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;

/** Ant task wrapping a test run.*/
public class SlcTestTask extends SAwareTask {

	private TestDefinitionArg testDefinitionArg;
	private TestDataArg testDataArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		TestDefinition testDefinition = testDefinitionArg.getTestDefinition();
		testDefinition.setTestData(testDataArg.getTestData());
		testDefinition.execute();
	}	

	public TestDefinitionArg createTestDefinition() {
		testDefinitionArg = new TestDefinitionArg();
		sAwareArgs.add(testDefinitionArg);
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		sAwareArgs.add(testDataArg);
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
