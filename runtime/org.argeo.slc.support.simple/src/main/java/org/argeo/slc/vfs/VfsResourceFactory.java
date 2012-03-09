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
package org.argeo.slc.vfs;

import org.apache.commons.vfs.CacheStrategy;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class VfsResourceFactory implements FactoryBean, InitializingBean {
	private String url;
	private FileSystemManager fileSystemManager;

	public Object getObject() throws Exception {
		return new VfsResource(fileSystemManager.resolveFile(url));
	}

	public Class<?> getObjectType() {
		return Resource.class;
	}

	public boolean isSingleton() {
		return false;
	}

	public void afterPropertiesSet() throws Exception {
		if (fileSystemManager == null) {
			fileSystemManager = new StandardFileSystemManager();
			((StandardFileSystemManager) fileSystemManager)
					.setCacheStrategy(CacheStrategy.ON_RESOLVE);
			((StandardFileSystemManager) fileSystemManager).init();
		}

	}

}
