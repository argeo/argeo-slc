package org.argeo.slc.support.deploy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.TargetData;

public class HttpdApplicationTargetData implements TargetData {
	private HttpdServer webServer;
	private String relativePath;
	private String targetRootPath;

	public HttpdServer getWebServer() {
		return webServer;
	}

	public void setWebServer(HttpdServer webServer) {
		this.webServer = webServer;
	}

	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * If targetRootLocation not set, used to build the targetRootLocation,
	 * relative to the webserver base.
	 */
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getTargetRootPath() {
		return targetRootPath;
	}

	public void setTargetRootPath(String targetRootPath) {
		this.targetRootPath = targetRootPath;
	}

	public URL getTargetBaseUrl() {
		try {
			URL wsUrl = getWebServer().getBaseUrl();
			// TODO: use URI
			return new URL(wsUrl, wsUrl.getFile() + '/' + relativePath);
		} catch (MalformedURLException e) {
			throw new SlcException("Cannot get base url for " + relativePath, e);
		}
	}

	public File getTargetRootLocation() {
		if (targetRootPath != null && !targetRootPath.equals("")) {
			return new File(targetRootPath);
		} else {
			HttpdServerTargetData targetData = (HttpdServerTargetData) getWebServer()
					.getTargetData();
			String path = targetData.getServerRoot() + File.separator
					+ getRelativePath();
			return new File(path);
		}
	}

}
