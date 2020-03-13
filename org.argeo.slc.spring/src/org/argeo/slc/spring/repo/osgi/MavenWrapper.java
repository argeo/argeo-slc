package org.argeo.slc.spring.repo.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;

public class MavenWrapper extends org.argeo.slc.repo.osgi.MavenWrapper implements BeanNameAware {
	private final static Log log = LogFactory.getLog(MavenWrapper.class);

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
