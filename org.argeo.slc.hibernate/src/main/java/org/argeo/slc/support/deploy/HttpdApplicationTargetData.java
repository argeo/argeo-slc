package org.argeo.slc.support.deploy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdApplicationTargetData implements TargetData {
	private ApacheHttpdServer webServer;
	private String relativePath;

	public ApacheHttpdServer getWebServer() {
		return webServer;
	}

	public void setWebServer(ApacheHttpdServer webServer) {
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
			return new URL(wsUrl, wsUrl.getFile() + '/' + relativePath);
		} catch (MalformedURLException e) {
			throw new SlcException("Cannot get base url for " + relativePath, e);
		}
	}

	public File getTargetRootLocation() {
		return new File(getWebServer().getBaseLocation().getPath()
				+ File.separator + getRelativePath());
	}

}
