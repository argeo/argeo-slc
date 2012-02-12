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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.VersioningDriver;

/**
 * Synchronizes an URL to a local directory, taking into account versioning
 * information if possible.
 */
public class VersionedDirSync implements Runnable {
	private final static Log log = LogFactory.getLog(VersionedDirSync.class);

	private VersioningDriver versioningDriver;
	private File dir;
	private String url;
	private Boolean clean = false;

	private Boolean changed = null;

	public void run() {
		changed = null;
		if (clean) {
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				throw new SlcException("Cannot delete checkout directory "
						+ dir, e);
			}
			dir.mkdirs();
		}
		changed = versioningDriver.checkout(url, dir, true);
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

	/** Delete before checkout */
	public void setClean(Boolean clean) {
		this.clean = clean;
	}

	/** Whether last call has changed the directory */
	public Boolean getChanged() {
		if (changed == null)
			throw new SlcException("Sync has not run");
		return changed;
	}

}
