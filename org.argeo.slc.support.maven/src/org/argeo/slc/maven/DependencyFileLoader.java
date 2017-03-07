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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

public class DependencyFileLoader implements FactoryBean{
	private final static Log log = LogFactory.getLog(DependencyFileLoader.class);

	private Resource dependenciesResource;

	@SuppressWarnings("unchecked")
	public List<MavenFile> loadMavenFiles() {
		try {
			List<MavenFile> mavenFiles = new ArrayList<MavenFile>();

			List<String> lines = IOUtils.readLines(dependenciesResource
					.getInputStream());
			for (String line : lines) {
				try {
					line = line.trim();
					if (line.equals("")
							|| line
									.startsWith("The following files have been resolved:"))
						continue;// skip

					mavenFiles.add(convert(line));
				} catch (Exception e) {
					log.warn("Could not load line " + line);
				}
			}

			return mavenFiles;
		} catch (IOException e) {
			throw new SlcException("Could not read dependencies resource "
					+ dependenciesResource, e);
		}
	}

	protected MavenFile convert(String str) {
		StringTokenizer st = new StringTokenizer(str, ":");
		MavenFile component = new MavenFile();
		component.setGroupId(st.nextToken());
		component.setArtifactId(st.nextToken());
		component.setType(st.nextToken());
		component.setVersion(st.nextToken());
		component.setScope(st.nextToken());
		return component;
	}

	public void setDependenciesResource(Resource dependenciesResource) {
		this.dependenciesResource = dependenciesResource;
	}

	public Object getObject() throws Exception {
		return loadMavenFiles();
	}

	@SuppressWarnings("unchecked")
	public Class<List> getObjectType() {
		return List.class;
	}

	public boolean isSingleton() {
		return false;
	}

	
}
