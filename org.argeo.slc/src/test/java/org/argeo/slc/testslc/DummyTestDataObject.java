package org.argeo.slc.testslc;

import org.argeo.slc.core.structure.tree.DefaultTreeSAware;

public class DummyTestDataObject extends DefaultTreeSAware{
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
