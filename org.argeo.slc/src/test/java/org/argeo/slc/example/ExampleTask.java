package org.argeo.slc.example;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestRun;

public class ExampleTask implements StructureAware,TestDefinition{
	private TreeSPath path;
	private String description;

	public void execute(TestRun testRun){
		SimpleResultPart part = new SimpleResultPart();
		part.setStatus(SimpleResultPart.PASSED);
		part.setMessage("Sub task executed");
		
		TestResult result = testRun.getTestResult();
		if(result instanceof StructureAware){
			((StructureAware)result).notifyCurrentPath(null, path);
		}
		result.addResultPart(part);
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.path = (TreeSPath)path;		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
