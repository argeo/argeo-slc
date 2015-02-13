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
package org.argeo.slc.diff;

/**
 * A value missing in one of the file. If its position is related to expected,
 * this means it is a left over in the reached, if its position is related to
 * the reached it means that it is missing from the reached. If the value is
 * null it means that the entire line is missing.
 */
public class DiffMissing extends DiffIssueKey {

	public DiffMissing(DiffPosition position, DiffKey key) {
		super(position);
		super.key = key;
	}

	@Override
	public String toString() {
		if (position.relatedFile == RelatedFile.EXPECTED) {
			return position + ": left over " + super.toString();
		} else if (position.relatedFile == RelatedFile.REACHED) {
			return position + ": missing " + super.toString();
		}
		return super.toString();
	}

}
