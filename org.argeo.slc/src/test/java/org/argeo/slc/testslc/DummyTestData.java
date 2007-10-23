package org.argeo.slc.testslc;

import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.test.TestData;

public class DummyTestData extends DefaultTreeSAware implements TestData {
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

	@Override
	public void onRegister(StructureRegistry registry) {
		if (expected instanceof TreeSAware) {
			TreeSAware sAware = (TreeSAware) expected;
			TreeSElement element = ((TreeSElement) getElement()).createChild(
					"expected" + getChildren().size(), "<no desc>");
			sAware.setElement(element);
			addChild(sAware);
		}
		super.onRegister(registry);
	}
	
	

}
