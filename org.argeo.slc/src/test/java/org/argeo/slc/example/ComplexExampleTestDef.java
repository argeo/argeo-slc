package org.argeo.slc.example;

import java.util.List;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestRun;

public class ComplexExampleTestDef implements TestDefinition, StructureAware {
	private List<ExampleTask> tasks;

	public void execute(TestRun testRun) {
		// TODO Auto-generated method stub

	}

	public void setTasks(List<ExampleTask> tasks) {
		this.tasks = tasks;
	}

	public StructureElement getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		// TODO Auto-generated method stub
		
	}

	
}
