package org.argeo.slc.aether.spring;

import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

/** Simplifies the definition of a remote factory using Spring */
public class RemoteRepositoryFactory implements BeanNameAware, FactoryBean {
	private String beanName;
	private String id;
	private String url;
	private String type = "default";
	private String username;
	private String password;

	public Object getObject() throws Exception {
		RemoteRepository remoteRepository = new RemoteRepository(
				id != null ? id : beanName, type, url);
		if (username != null) {
			Authentication authentication = new Authentication(username,
					password);
			remoteRepository.setAuthentication(authentication);
		}
		return remoteRepository;
	}

	public Class<?> getObjectType() {
		return RemoteRepository.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setBeanName(String name) {
		this.beanName = name;

	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
