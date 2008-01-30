package org.argeo.slc.core.test.context;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.InitializingBean;

public class SimpleParentContextAware extends SimpleContextAware implements
		ParentContextAware, InitializingBean {
	private List<ContextAware> children = new Vector<ContextAware>();

	public Collection<ContextAware> getChildContexts() {
		return children;
	}

	public void addChildContext(ContextAware contextAware) {
		children.add(contextAware);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (getParentContext() != null) {
			// If has a parent, sync it.
			super.afterPropertiesSet();
		} else {
			ContextUtils.synchronize(this);
		}
	}
}
