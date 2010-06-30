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

package org.argeo.slc.diff;

/** Diff issue where reached and expected values are different. */
public class DiffNotMatched extends DiffIssueKey {
	private final Object expected;
	private final Object reached;

	public DiffNotMatched(DiffPosition position, Object expected, Object reached) {
		super(position);
		this.expected = expected;
		this.reached = reached;
	}

	public DiffNotMatched(DiffPosition position, Object expected,
			Object reached, DiffKey key) {
		super(position, key);
		this.expected = expected;
		this.reached = reached;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getReached() {
		return reached;
	}

	@Override
	public String toString() {
		String result = position + ": not matched " + expected + " <> "
				+ reached;
		if (super.key != null) {
			result = result + " - Key: " + super.toString();
		}

		return result;
	}

}
