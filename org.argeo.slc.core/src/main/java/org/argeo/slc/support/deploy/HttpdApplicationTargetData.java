package org.argeo.slc.support.deploy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdApplicationTargetData implements TargetData {
	private HttpdServer webServer;
	private String relativePath;

	public HttpdServer getWebServer() {
		return webServer;
	}

	public void setWebServer(HttpdServer webServer) {
		this.webServer = webServer;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
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
		HttpdServerTargetData targetData = (HttpdServerTargetData) getWebServer()
				.getTargetData();
		String path = targetData.getServerRoot() + File.separator
				+ getRelativePath();
		return new File(path);
	}

}
