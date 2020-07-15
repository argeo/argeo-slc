package org.argeo.slc.lib.vbox;

import java.util.Map;

/** The NAT mapping table */
public class VBoxNat {
	private String device = "1";
	private String guestIp = "";
	private Map<String, VBoxPortMapping> mappings;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Map<String, VBoxPortMapping> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, VBoxPortMapping> mappings) {
		this.mappings = mappings;
	}

	public String getGuestIp() {
		return guestIp;
	}

	public void setGuestIp(String guestIp) {
		this.guestIp = guestIp;
	}

}
