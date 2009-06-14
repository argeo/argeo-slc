package org.argeo.slc.lib.vbox;

/** The mapping of one port.*/
public class VBoxPortMapping {
	private String protocol = "TCP";
	private String guest;
	private String host;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String type) {
		this.protocol = type;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
