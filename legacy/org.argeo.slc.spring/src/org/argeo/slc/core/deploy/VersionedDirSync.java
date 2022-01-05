package org.argeo.slc.core.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.VersioningDriver;

/**
 * Synchronizes an URL to a local directory, taking into account versioning
 * information if possible.
 */
public class VersionedDirSync implements Runnable {
	private final static CmsLog log = CmsLog.getLog(VersionedDirSync.class);

	private VersioningDriver versioningDriver;
	private File dir;
	private String url;
	private Boolean clean = false;

	private Boolean changed = null;

	public void run() {
		changed = null;
		if (clean) {
			try {
				log.info("Clean " + dir);
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				throw new SlcException("Cannot delete checkout directory "
						+ dir, e);
			}
			dir.mkdirs();
		}
		log.info("Checkout " + url + " to " + dir);
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
