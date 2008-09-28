package org.argeo.slc.detached;

public interface DetachedClient {
	public void sendRequest(DetachedStepRequest request) throws Exception;

	/** Blocks until next answer. */
	public DetachedStepAnswer receiveAnswer() throws Exception;
}
