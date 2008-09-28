package org.argeo.slc.detached;

public interface DetachedExecutionServer {
	public DetachedStepAnswer executeStep(DetachedStepRequest request);
}
