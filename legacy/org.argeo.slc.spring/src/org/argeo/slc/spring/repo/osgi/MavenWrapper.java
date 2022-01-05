package org.argeo.slc.spring.repo.osgi;

import org.argeo.api.cms.CmsLog;
import org.springframework.beans.factory.BeanNameAware;

public class MavenWrapper extends org.argeo.slc.repo.osgi.MavenWrapper implements BeanNameAware {
	private final static CmsLog log = CmsLog.getLog(MavenWrapper.class);

	@Override
	public void setBeanName(String name) {
		if (getName() == null) {
			setName(name);
		} else {
			if (!name.contains("#"))
				log.warn("Using explicitely set name " + getName() + " and not bean name " + name);
		}
	}

}
