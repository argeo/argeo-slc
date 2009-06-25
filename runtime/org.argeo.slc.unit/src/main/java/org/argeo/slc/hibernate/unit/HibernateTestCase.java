package org.argeo.slc.hibernate.unit;

import org.argeo.slc.unit.AbstractSpringTestCase;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernateTestCase extends AbstractSpringTestCase {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		if (hibernateTemplate == null) {
			hibernateTemplate = new HibernateTemplate(
					getBean(SessionFactory.class));
		}

		return hibernateTemplate;
	}

}
