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

package org.argeo.slc.core.deploy;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.deploy.VersioningDriver;

public class VersionedDirSync implements Runnable {
	private final static Log log = LogFactory.getLog(VersionedDirSync.class);

	private VersioningDriver versioningDriver;
	private File dir;
	private String url;

	public void run() {
		versioningDriver.checkout(url, dir, true);
		if (log.isDebugEnabled())
			log.debug("Synchronized " + url + " to " + dir);
	}

	public void setVersioningDriver(VersioningDriver versioningDriver) {
		this.versioningDriver = versioningDriver;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
