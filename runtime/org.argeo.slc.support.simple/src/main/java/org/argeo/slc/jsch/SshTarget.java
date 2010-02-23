package org.argeo.slc.jsch;

import java.io.File;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshTarget {
	private String host;
	private Integer port = 22;
	private String user;
	private UserInfo userInfo;

	private Boolean usePrivateKey = true;
	private File localPrivateKey = new File(System.getProperty("user.home")
			+ File.separator + ".ssh" + File.separator + "id_rsa");

	/** cached session */
	private Session session;

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
		return "ssh:" + getUser() + "@" + getHost() + ":" + getPort();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
