package org.argeo.slc.autoui;


public interface DetachedStep {
	public DetachedStepAnswer execute(DetachedContext detachedContext,
			DetachedStepRequest detachedStepRequest);
}
