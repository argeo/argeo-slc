package org.argeo.slc.jsch;

import com.jcraft.jsch.UserInfo;

public class SshTarget {
	private String host;
	private Integer port;
	private String user;
	private UserInfo userInfo;

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

	public String toString() {
		return "ssh:" + getUser() + "@" + getHost() + ":" + getPort();
	}
}
