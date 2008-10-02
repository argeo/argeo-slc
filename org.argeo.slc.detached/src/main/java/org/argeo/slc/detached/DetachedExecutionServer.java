package org.argeo.slc.detached;

public interface DetachedExecutionServer {
	public DetachedAnswer executeStep(DetachedRequest request);
}
