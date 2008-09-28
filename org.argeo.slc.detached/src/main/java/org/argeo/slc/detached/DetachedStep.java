package org.argeo.slc.detached;


public interface DetachedStep {
	public DetachedStepAnswer execute(DetachedContext detachedContext,
			DetachedStepRequest detachedStepRequest);
}
