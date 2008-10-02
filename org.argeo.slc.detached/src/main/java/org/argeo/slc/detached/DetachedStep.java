package org.argeo.slc.detached;


public interface DetachedStep {
	public DetachedAnswer execute(DetachedContext detachedContext,
			DetachedRequest detachedStepRequest);
}
