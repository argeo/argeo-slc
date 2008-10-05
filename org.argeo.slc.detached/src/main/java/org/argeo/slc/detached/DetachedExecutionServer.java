package org.argeo.slc.detached;

public interface DetachedExecutionServer {
	public DetachedAnswer executeRequest(DetachedRequest request);
}
