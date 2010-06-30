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

package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.NameVersion;

public interface DynamicRuntime<M extends Module> extends
		ModularDeployedSystem<M> {
	public void shutdown();

	public M installModule(Distribution distribution);

	public void uninstallModule(NameVersion nameVersion);

	public void startModule(NameVersion nameVersion);

}
