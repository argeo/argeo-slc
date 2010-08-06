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

/** The root class for issues which happened during a diff. */
public abstract class DiffIssue implements Comparable<DiffIssue> {
	/** The position of this issue. */
	// Was final and is not anymore in order to persist in hibernate
	protected DiffPosition position;

	// hibernate
	private long id;

	/** Constructor */
	public DiffIssue(DiffPosition position) {
		super();
		this.position = position;
	}

	public int compareTo(DiffIssue o) {
		return position.compareTo(o.position);
	}

	/** The position of this issue within the test file */
	public DiffPosition getPosition() {
		return position;
	}

	// Hibernate
	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setPosition(DiffPosition position) {
		this.position = position;
	}

}
