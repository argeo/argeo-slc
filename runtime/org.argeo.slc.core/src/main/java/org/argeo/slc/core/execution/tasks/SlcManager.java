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
package org.argeo.slc.core.execution.tasks;

import java.lang.reflect.Method;
import java.util.UUID;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeployedSystemManager;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionRelated;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.structure.StructureRegistry;

/** Use {@link MethodCall} instead. */
@Deprecated
public class SlcManager implements Runnable, SlcExecutionRelated {
	private String uuid;
	private String slcExecutionUuid;
	private String slcExecutionStepUuid;

	private String action;
	private DeployedSystemManager<DeployedSystem> manager;

	public final void run() {
		uuid = UUID.randomUUID().toString();
		executeActions(StructureRegistry.ALL);
	}

	protected void executeActions(String mode) {
		try {
			Class<?>[] argClasses = null;
			Method method = manager.getClass().getMethod(action, argClasses);
			Object[] argObjects = null;
			method.invoke(manager, argObjects);
		} catch (Exception e) {
			throw new SlcException("Cannot execute action " + action
					+ " for manager " + manager, e);
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setManager(DeployedSystemManager<DeployedSystem> manager) {
		this.manager = manager;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public void notifySlcExecution(SlcExecution slcExecution) {
		if (slcExecution != null) {
			slcExecutionUuid = slcExecution.getUuid();
			SlcExecutionStep step = slcExecution.currentStep();
			if (step != null) {
				slcExecutionStepUuid = step.getUuid();
			}
		}
	}
}
