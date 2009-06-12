package org.argeo.slc.example;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureElementProvider;
import org.argeo.slc.structure.StructureRegistry;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestRun;

/** Example task. */
public class ExampleTask implements StructureAware<TreeSPath>, TestDefinition,
		StructureElementProvider {
	private TreeSPath path;
	private String description;

	public void execute(TestRun testRun) {
		SimpleResultPart part = new SimpleResultPart(PASSED,
				"Sub task with path " + path + " executed", null);

		TestResult result = testRun.getTestResult();
		result.addResultPart(part);
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		this.path = path;
	}

	public StructureElement createStructureElement() {
		return new SimpleSElement(description);
	}

	/** Sets the description. */
	public void setDescription(String description) {
		this.description = description;
	}

}
