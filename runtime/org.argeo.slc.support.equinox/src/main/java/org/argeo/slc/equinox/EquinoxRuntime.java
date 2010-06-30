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

package org.argeo.slc.equinox;

import org.argeo.slc.SlcException;
import org.argeo.slc.osgi.OsgiRuntime;
import org.eclipse.core.runtime.adaptor.EclipseStarter;

public class EquinoxRuntime extends OsgiRuntime {

	public void shutdown() {
		try {
			EclipseStarter.shutdown();
		} catch (Exception e) {
			throw new SlcException("Cannot shutdown Equinox runtime.", e);
		}
	}

}
