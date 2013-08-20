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
package org.argeo.slc;

import java.io.Serializable;

/** @deprecated use {@link DefaultNameVersion} instead. */
@Deprecated
public class BasicNameVersion extends DefaultNameVersion implements
		Serializable {
	private static final long serialVersionUID = -5127304279136195127L;

	public BasicNameVersion() {
	}

	/** Interprets string in OSGi-like format my.module.name;version=0.0.0 */
	public BasicNameVersion(String nameVersion) {
		int index = nameVersion.indexOf(";version=");
		if (index < 0) {
			setName(nameVersion);
			setVersion(null);
		} else {
			setName(nameVersion.substring(0, index));
			setVersion(nameVersion.substring(index + ";version=".length()));
		}
	}

	public BasicNameVersion(String name, String version) {
		super(name, version);
	}

	public BasicNameVersion(NameVersion nameVersion) {
		super(nameVersion);
	}
}
