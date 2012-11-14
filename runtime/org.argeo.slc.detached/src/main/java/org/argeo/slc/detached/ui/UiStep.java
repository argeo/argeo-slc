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
package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedStep;
import org.springframework.beans.factory.BeanNameAware;

public abstract class UiStep implements DetachedStep, BeanNameAware {
	private String beanName;

	public final DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest detachedStepRequest) {
		// uiPart.init(detachedContext, detachedStepRequest);
		return executeUiStep(detachedContext, detachedStepRequest);
	}

	protected abstract DetachedAnswer executeUiStep(DetachedContext context,
			DetachedRequest request);

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public String getBeanName() {
		return beanName;
	}

}
