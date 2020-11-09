package org.argeo.slc.vfs;

import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
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
