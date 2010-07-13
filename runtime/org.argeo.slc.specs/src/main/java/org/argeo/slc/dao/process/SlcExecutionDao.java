/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.dao.process;

import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

public interface SlcExecutionDao {
	public void create(SlcExecution slcExecution);

	public void update(SlcExecution slcExecution);

	@Deprecated
	public void merge(SlcExecution slcExecution);

	public SlcExecution getSlcExecution(String uuid);

	public List<SlcExecution> listSlcExecutions();

	public void addSteps(String slcExecutionId,
			List<SlcExecutionStep> additionalSteps);

	public List<SlcExecutionStep> tailSteps(final String slcExecutionId,
			final Integer nbrOfSteps);

	public List<SlcExecutionStep> tailSteps(final String slcExecutionId,
			final String slcExecutionStepId);
}
