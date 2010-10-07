package org.argeo.slc.web.ajaxplorer.file;

import java.io.File;

import org.argeo.slc.web.ajaxplorer.SimpleAjxpDriver;

public class FileDriver extends SimpleAjxpDriver{
	private String basePath;
	private String encoding = "UTF-8";

	public String getBasePath() {
		return basePath;
	}
	
	public File getBaseDir(){
		return new File(getBasePath());
	}

	public void setBasePath(String basePath) {
		if (basePath.charAt(basePath.length() - 1) != File.separatorChar)
			basePath = basePath + File.separatorChar;
		this.basePath = basePath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public File getFile(String relpath) {
		return new File(getBasePath() + relpath).getAbsoluteFile();
	}

	public File getFile(String dir, String fileName) {
		return getFile(dir + File.separator + fileName);
	}
}
