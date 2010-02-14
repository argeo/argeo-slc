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
