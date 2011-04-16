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

package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.io.Writer;

import org.argeo.slc.NameVersion;
import org.argeo.slc.build.ModularDistribution;

/** List of modules for a distribution. */
public class AvailableModulesPlain extends AbstractAvailableModules {
	@Override
	protected void print(Writer out, String baseUrl, ModularDistribution md)
			throws IOException {
		for (NameVersion nameVersion : md.listModulesNameVersions()) {
			String fileName = nameVersion.getName() + "-"
					+ nameVersion.getVersion() + ".jar";
			String moduleUrl = baseUrl + fileName;
			out.write(moduleUrl);
			out.write("\n");
		}
	}
}
