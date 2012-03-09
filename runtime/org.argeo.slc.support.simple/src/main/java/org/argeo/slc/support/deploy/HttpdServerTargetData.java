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
package org.argeo.slc.support.deploy;

import org.argeo.slc.deploy.InstalledExecutables;
import org.argeo.slc.deploy.TargetData;

public class HttpdServerTargetData implements TargetData {
	private String serverRoot;
	private Integer port;
	private InstalledExecutables executables;

	public String getServerRoot() {
		return serverRoot;
	}

	public void setServerRoot(String serverRoot) {
		this.serverRoot = serverRoot;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public InstalledExecutables getExecutables() {
		return executables;
	}

	public void setExecutables(InstalledExecutables executables) {
		this.executables = executables;
	}

}
