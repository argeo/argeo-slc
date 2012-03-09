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
package org.argeo.slc.diff;

/** The position of a diff issue within the test resource. */
public abstract class DiffPosition implements Comparable<DiffPosition> {
	protected RelatedFile relatedFile;

	public DiffPosition(RelatedFile relatedFile) {
		super();
		this.relatedFile = relatedFile;
	}

	// For Hibernate
	DiffPosition() {
	}

	public RelatedFile getRelatedFile() {
		return relatedFile;
	}

	// Added to enable the new data model for persisting TabularDiffTestResult
	@SuppressWarnings("unused")
	private Boolean getIsReached() {
		return relatedFile.equals(RelatedFile.REACHED);
	}

	@SuppressWarnings("unused")
	private void setIsReached(Boolean isReached) {
		this.relatedFile = (isReached ? RelatedFile.REACHED
				: RelatedFile.EXPECTED);
	}

}
