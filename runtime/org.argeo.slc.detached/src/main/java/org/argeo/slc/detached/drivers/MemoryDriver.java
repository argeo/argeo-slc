package org.argeo.slc.detached.drivers;

import java.util.Stack;

import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedClient;
import org.argeo.slc.detached.DetachedRequest;

public class MemoryDriver extends AbstractDriver implements DetachedClient {
	private final static Stack stack = new Stack();

	// DRIVER
	public DetachedRequest receiveRequest() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendAnswer(DetachedAnswer answer) throws Exception {
		// TODO Auto-generated method stub

	}

	// CLIENT
	public DetachedAnswer receiveAnswer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendRequest(DetachedRequest request) throws Exception {
		// TODO Auto-generated method stub
		
	}

}