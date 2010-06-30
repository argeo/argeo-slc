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

package org.argeo.slc.msg;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList {
	private List<String> references = new ArrayList<String>();

	public ReferenceList() {
	}

	public ReferenceList(List<String> references) {
		this.references = references;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> refs) {
		this.references = refs;
	}

}
