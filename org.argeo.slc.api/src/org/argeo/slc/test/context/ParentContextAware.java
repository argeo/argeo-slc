package org.argeo.slc.test.context;

import java.util.Collection;

public interface ParentContextAware extends ContextAware {
	public Collection<ContextAware> getChildContexts();

	public void addChildContext(ContextAware contextAware);
}
