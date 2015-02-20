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
package org.argeo.slc.aether.spring;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

/** Simplifies the definition of a remote factory using Spring */
public class RemoteRepositoryFactory implements BeanNameAware,
		FactoryBean<RemoteRepository> {
	private String beanName;
	private String id;
	private String url;
	private String type = "default";
	private String username;
	private String password;

	public RemoteRepository getObject() throws Exception {
		RemoteRepository.Builder builder = new RemoteRepository.Builder(
				id != null ? id : beanName, type, url);
		if (username != null) {
			AuthenticationBuilder authBuilder = new AuthenticationBuilder();
			authBuilder.addUsername(username);
			authBuilder.addPassword(password);
			builder.setAuthentication(authBuilder.build());
		}
		RemoteRepository remoteRepository = builder.build();
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
