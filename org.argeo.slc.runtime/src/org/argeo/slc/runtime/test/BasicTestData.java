package org.argeo.slc.runtime.test;

import org.argeo.slc.test.TestData;

public class BasicTestData implements TestData {
	private Object expected;
	private Object reached;

	public Object getExpected() {
		return expected;
	}

	public void setExpected(Object expected) {
		this.expected = expected;
	}

	public Object getReached() {
		return reached;
	}

	public void setReached(Object reached) {
		this.reached = reached;
	}

}
