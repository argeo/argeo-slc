/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.jsch;

import java.io.File;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshTarget {
	private String host;
	private Integer port = 22;
	private String user;
	private UserInfo userInfo = new SimpleUserInfo();

	private Boolean usePrivateKey = true;
	private File localPrivateKey = new File(System.getProperty("user.home")
			+ File.separator + ".ssh" + File.separator + "id_rsa");

	/** cached session */
	private transient Session session;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void setLocalPrivateKey(File localPrivateKey) {
		this.localPrivateKey = localPrivateKey;
	}

	public File getLocalPrivateKey() {
		return localPrivateKey;
	}

	public Boolean getUsePrivateKey() {
		return usePrivateKey;
	}

	public void setUsePrivateKey(Boolean usePrivateKey) {
		this.usePrivateKey = usePrivateKey;
	}

	public String toString() {
		return getUser() + "@" + getHost() + ":" + getPort();
	}

	public synchronized Session getSession() {
		return session;
	}

	public synchronized void setSession(Session session) {
		this.session = session;
	}
}
