package org.argeo.slc.detached;

public interface DetachedClient {
	public void sendRequest(DetachedRequest request) throws Exception;

	/** Blocks until next answer. */
	public DetachedAnswer receiveAnswer() throws Exception;
}
