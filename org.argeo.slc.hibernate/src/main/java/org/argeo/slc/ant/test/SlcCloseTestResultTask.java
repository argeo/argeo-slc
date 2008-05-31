package org.argeo.slc.ant.test;

import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.test.TestResult;

/** Ant tasks closing a given result. */
public class SlcCloseTestResultTask extends SAwareTask {
	private String result;

	@Override
	public void executeActions(String mode) {
		if (!mode.equals(StructureRegistry.READ)) {
			TestResult testResult = (TestResult) getContext().getBean(result);
			testResult.close();
		}
	}

	/** Sets the bean name of the result to close. */
	public void setResult(String bean) {
		this.result = bean;
	}

}
