package org.argeo.slc.testslc;

import org.argeo.slc.core.test.TestData;

public class DummyTestData implements TestData {
	private Object reached;
	private Object expected;

	public Object getReached() {
		return reached;
	}

	public void setReached(Object reached) {
		this.reached = reached;
	}

	public Object getExpected() {
		return expected;
	}

	public void setExpected(Object expected) {
		this.expected = expected;
	}

}
