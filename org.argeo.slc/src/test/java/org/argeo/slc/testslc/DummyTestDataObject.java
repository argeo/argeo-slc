package org.argeo.slc.testslc;

public class DummyTestDataObject {
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DummyTestDataObject) {
			DummyTestDataObject dtdo = (DummyTestDataObject) obj;
			return value.equals(dtdo.value);
		}
		return false;
	}

}
