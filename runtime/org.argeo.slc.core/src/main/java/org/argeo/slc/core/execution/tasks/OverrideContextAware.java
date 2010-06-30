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

package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.context.SimpleContextAware;
import org.argeo.slc.test.context.ContextAware;

/**
 * Overrides Values and Expected values of a target 
 * <code>SimpleContextAware</code> with the corresponding
 * values and expected values of a source <code>ContextAware</code>
 *
 */
public class OverrideContextAware implements Runnable {

	private ContextAware source;

	private SimpleContextAware target;
	
	/**
	 * Whether an exception shall be thrown if a value
	 * or expected value of the source is not defined
	 * in the target
	 */
	private Boolean failIfUndefinedInSource = true;
	
	public void run() {
		// override values
		if(source.getValues() != null)
			for(String key : source.getValues().keySet()) {
				if(failIfUndefinedInSource && !target.getValues().containsKey(key)) {
					throw new SlcException("No entry in target values for key '" + key + "'");
				}
				target.getValues().put(key, source.getValues().get(key));
			}
		
		// override expected values
		if(source.getExpectedValues() != null)
			for(String key : source.getExpectedValues().keySet()) {
				if(failIfUndefinedInSource && !target.getExpectedValues().containsKey(key)) {
					throw new SlcException("No entry in target expected values for key '" + key + "'");
				}
				target.getExpectedValues().put(key, source.getExpectedValues().get(key));
			}		
	}	
	
	public void setSource(ContextAware source) {
		this.source = source;
	}

	public void setTarget(SimpleContextAware target) {
		this.target = target;
	}

	public void setFailIfUndefinedInSource(Boolean failIfUndefinedInSource) {
		this.failIfUndefinedInSource = failIfUndefinedInSource;
	}	
}
