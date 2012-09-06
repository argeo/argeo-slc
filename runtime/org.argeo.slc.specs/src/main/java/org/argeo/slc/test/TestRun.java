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

import org.argeo.slc.deploy.DeployedSystem;

/** The actual run of a test */
public interface TestRun {
	/** Gets UUID */
	public String getUuid();

	/** Gets the related test definition. */
	public <T extends TestDefinition> T getTestDefinition();

	/** Gets the related test data */
	public <T extends TestData> T getTestData();

	/** Gets the related deployed system. */
	public <T extends DeployedSystem> T getDeployedSystem();

	/** Gets the related result where to record results. */
	public <T extends TestResult> T getTestResult();
}
