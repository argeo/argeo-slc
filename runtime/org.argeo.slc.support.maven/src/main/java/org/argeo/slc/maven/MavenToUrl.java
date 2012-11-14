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
package org.argeo.slc.maven;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

public class MavenToUrl implements FactoryBean {
	private List<MavenFile> mavenFiles;
	private String baseUrl;

	public List<String> asUrls() {
		List<String> urls = new ArrayList<String>();
		for (MavenFile mf : mavenFiles)
			urls.add(convertToUrl(mf));
		return urls;
	}

	public String convertToUrl(MavenFile mf) {
		return baseUrl + mf.getGroupId().replace('.', '/') + '/'
				+ mf.getArtifactId() + '/' + mf.getVersion() + '/'
				+ mf.getArtifactId() + '-' + mf.getVersion() + '.'
				+ mf.getType();
	}

	public void setMavenFiles(List<MavenFile> mavenFiles) {
		this.mavenFiles = mavenFiles;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Object getObject() throws Exception {
		return asUrls();
	}

	public Class<?> getObjectType() {
		return List.class;
	}

	public boolean isSingleton() {
		return false;
	}

}
