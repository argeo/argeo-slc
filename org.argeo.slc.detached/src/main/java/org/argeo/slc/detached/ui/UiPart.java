package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;

public interface UiPart {
	public void init(DetachedContext context, DetachedRequest request);
}
