package org.argeo.slc.spring.repo.osgi;

import org.argeo.api.cms.CmsLog;
import org.springframework.beans.factory.BeanNameAware;

public class UriWrapper extends org.argeo.slc.repo.osgi.UriWrapper implements BeanNameAware {
	private final static CmsLog log = CmsLog.getLog(UriWrapper.class);

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
