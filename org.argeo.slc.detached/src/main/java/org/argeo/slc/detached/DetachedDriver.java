package org.argeo.slc.detached;

public interface DetachedDriver {
	/** Blocks until it receives a request. */
	public DetachedRequest receiveRequest() throws Exception;
	public void sendAnswer(DetachedAnswer answer) throws Exception;
}
