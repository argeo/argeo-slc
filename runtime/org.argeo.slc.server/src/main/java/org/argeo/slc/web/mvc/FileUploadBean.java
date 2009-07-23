package org.argeo.slc.web.mvc;


public class FileUploadBean {
	private byte[] file;

	public void setFile(byte[] file) {
		this.file = file;
	}

	public byte[] getFile() {
		return file;
	}
}
