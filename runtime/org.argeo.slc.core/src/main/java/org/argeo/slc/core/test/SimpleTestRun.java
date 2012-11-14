/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.core.test;

import java.util.UUID;

import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.test.ExecutableTestRun;
import org.argeo.slc.test.TestData;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.WritableTestRun;

/**
 * A basic bean implementation of a <code>WritableTestRun</code>, holding
 * references to the various parts of a test run.
 */
public class SimpleTestRun implements WritableTestRun, ExecutableTestRun {
	private String uuid;

	// private String slcExecutionUuid;
	// private String slcExecutionStepUuid;

	private DeployedSystem deployedSystem;
	private TestData testData;
	private TestDefinition testDefinition;
	private TestResult testResult;

	/** Executes the underlying test definition. */
	public void run() {
		uuid = UUID.randomUUID().toString();
		if (testResult != null)
			testResult.notifyTestRun(this);

		testDefinition.execute(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends DeployedSystem> T getDeployedSystem() {
		return (T) deployedSystem;
	}

	public void setDeployedSystem(DeployedSystem deployedSystem) {
		this.deployedSystem = deployedSystem;
	}

	@SuppressWarnings("unchecked")
	public <T extends TestData> T getTestData() {
		return (T) testData;
	}

	public void setTestData(TestData testData) {
		this.testData = testData;
	}

	@SuppressWarnings("unchecked")
	public <T extends TestDefinition> T getTestDefinition() {
		return (T) testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	@SuppressWarnings("unchecked")
	public <T extends TestResult> T getTestResult() {
		return (T) testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	// public String getSlcExecutionUuid() {
	// return slcExecutionUuid;
	// }
	//
	// public void setSlcExecutionUuid(String slcExecutionUuid) {
	// this.slcExecutionUuid = slcExecutionUuid;
	// }
	//
	// public String getSlcExecutionStepUuid() {
	// return slcExecutionStepUuid;
	// }
	//
	// public void setSlcExecutionStepUuid(String slcExecutionStepUuid) {
	// this.slcExecutionStepUuid = slcExecutionStepUuid;
	// }
}
