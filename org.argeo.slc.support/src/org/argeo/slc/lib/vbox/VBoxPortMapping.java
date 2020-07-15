package org.argeo.slc.lib.vbox;

/** The mapping of one port. */
public class VBoxPortMapping {
	private String protocol = "TCP";
	private String guestPort;
	private String hostPort;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String type) {
		this.protocol = type;
	}

	public String getGuestPort() {
		return guestPort;
	}

	public void setGuestPort(String guestPort) {
		this.guestPort = guestPort;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

}
