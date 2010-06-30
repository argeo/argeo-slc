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

package org.argeo.slc.msg.build;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.build.BasicNameVersion;

public class ModularDistributionDescriptor extends BasicNameVersion implements
		Serializable {
	private static final long serialVersionUID = 1L;

	/** key is type, value the URL */
	private Map<String, String> modulesDescriptors = new HashMap<String, String>();

	public Map<String, String> getModulesDescriptors() {
		return modulesDescriptors;
	}

	public void setModulesDescriptors(Map<String, String> urls) {
		this.modulesDescriptors = urls;
	}

}
