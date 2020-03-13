package org.argeo.slc.spring.repo.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;

public class BndWrapper extends org.argeo.slc.repo.osgi.BndWrapper implements BeanNameAware {
	private final static Log log = LogFactory.getLog(BndWrapper.class);

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
