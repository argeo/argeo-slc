/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
