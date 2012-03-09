/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.test;

import java.io.Serializable;

import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.process.SlcExecutionRelated;

public class TestRunDescriptor implements Serializable {
	private static final long serialVersionUID = -6488010128523489018L;
	private String testRunUuid;
	private String slcExecutionUuid;
	private String slcExecutionStepUuid;
	private String testResultUuid;
	private String deployedSytemId;

	public TestRunDescriptor() {

	}

	public TestRunDescriptor(TestRun testRun) {
		testRunUuid = testRun.getUuid();

		if (testRun.getTestResult() != null)
			testResultUuid = testRun.<TestResult> getTestResult().getUuid();

		if (testRun.getDeployedSystem() != null)
			deployedSytemId = testRun.<DeployedSystem> getDeployedSystem()
					.getDeployedSystemId();

		if (testRun instanceof SlcExecutionRelated) {
			slcExecutionUuid = ((SlcExecutionRelated) testRun)
					.getSlcExecutionUuid();
			slcExecutionStepUuid = ((SlcExecutionRelated) testRun)
					.getSlcExecutionStepUuid();
		}
	}

	public String getTestRunUuid() {
		return testRunUuid;
	}

	public void setTestRunUuid(String testRunUuid) {
		this.testRunUuid = testRunUuid;
	}

	public String getSlcExecutionUuid() {
		return slcExecutionUuid;
	}

	public void setSlcExecutionUuid(String slcExecutionUuid) {
		this.slcExecutionUuid = slcExecutionUuid;
	}

	public String getSlcExecutionStepUuid() {
		return slcExecutionStepUuid;
	}

	public void setSlcExecutionStepUuid(String slcExecutionStepUuid) {
		this.slcExecutionStepUuid = slcExecutionStepUuid;
	}

	public String getTestResultUuid() {
		return testResultUuid;
	}

	public void setTestResultUuid(String testResultUuid) {
		this.testResultUuid = testResultUuid;
	}

	public String getDeployedSytemId() {
		return deployedSytemId;
	}

	public void setDeployedSytemId(String deploymentId) {
		this.deployedSytemId = deploymentId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestRunDescriptor) {
			return getTestRunUuid().equals(
					((TestRunDescriptor) obj).getTestRunUuid());
		}
		return false;
	}
}
