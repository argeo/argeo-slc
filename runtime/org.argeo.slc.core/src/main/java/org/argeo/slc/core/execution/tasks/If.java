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
package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;

/** Conditional execution */
public class If implements Runnable {
	private Boolean is;
	private Boolean not;
	private Runnable then;
	private Runnable els;

	public void run() {
		if (is == null && not == null)
			throw new SlcException("No condition set");
		if (is != null && not != null)
			throw new SlcException("Both is and not cannot be set");

		boolean bool = (is != null ? is : !not);
		if (bool) {
			if (then != null)
				then.run();
		} else {
			if (els != null)
				els.run();
		}

	}

	public void setIs(Boolean bool) {
		this.is = bool;
	}

	public void setThen(Runnable then) {
		this.then = then;
	}

	public void setEls(Runnable els) {
		this.els = els;
	}

	public Boolean getNot() {
		return not;
	}

	public void setNot(Boolean not) {
		this.not = not;
	}

}
