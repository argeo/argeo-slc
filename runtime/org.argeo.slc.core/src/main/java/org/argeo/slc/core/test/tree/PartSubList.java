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

package org.argeo.slc.core.test.tree;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;

/**
 * List of <code>TestResultPart</code>. It can be used to build complex
 * <code>TestResult</code> affording the possibility to a
 * <code>TestDefinition</code> to add a list of result part under the same
 * <code>StructurePath</code>.
 * 
 * @see TreeTestResult
 */
public class PartSubList implements Serializable {
	private static final long serialVersionUID = -5308754827565759844L;

	/** For ORM */
	private Long tid;

	private List<TestResultPart> parts = new Vector<TestResultPart>();

	/** Gets the result parts. */
	public List<TestResultPart> getParts() {
		return parts;
	}

	/** Sets the result parts. */
	public void setParts(List<TestResultPart> parts) {
		this.parts = parts;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	public Boolean getIsPassed() {
		for (TestResultPart part : parts) {
			if (part.getStatus() != TestStatus.PASSED) {
				return false;
			}
		}
		return true;
	}
}
