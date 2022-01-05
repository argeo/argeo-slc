package org.argeo.slc.spring.repo.osgi;

import org.argeo.api.cms.CmsLog;
import org.springframework.beans.factory.BeanNameAware;

public class BndWrapper extends org.argeo.slc.repo.osgi.BndWrapper implements BeanNameAware {
	private final static CmsLog log = CmsLog.getLog(BndWrapper.class);

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
