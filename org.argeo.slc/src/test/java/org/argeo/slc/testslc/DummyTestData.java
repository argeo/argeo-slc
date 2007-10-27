package org.argeo.slc.testslc;

import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.test.TestData;

public class DummyTestData extends DefaultTreeSAware implements TestData {
	private Object reached;
	private Object expected;

	public DummyTestData(){
		setElement(new TreeSElement("This is a dummy test data"));
	}
	
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
	public void onRegister(StructureRegistry registry, StructurePath path) {
		if (expected instanceof DefaultTreeSAware) {
			DefaultTreeSAware sAware = (DefaultTreeSAware) expected;
			TreeSElement element = new TreeSElement("This is an expected");
			element.setDescription("<no desc>");
			sAware.setElement(element);
			addToPropagationList("expected",sAware);
		}
		super.onRegister(registry, path);
	}
	
	

}
