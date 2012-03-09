/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
