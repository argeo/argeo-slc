package org.argeo.slc.core.test.context;

import java.util.Collection;

public interface ParentContextAware extends ContextAware {
	public Collection<ContextAware> getChildContexts();
}
