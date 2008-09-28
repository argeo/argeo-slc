package org.argeo.slc.autoui;

public interface DetachedDriver {
	/** Blocks until it receives a request. */
	public DetachedStepRequest receiveRequest() throws Exception;
	public void sendAnswer(DetachedStepAnswer answer) throws Exception;
}
