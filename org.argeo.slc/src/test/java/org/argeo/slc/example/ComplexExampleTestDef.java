package org.argeo.slc.example;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestRun;

public class ComplexExampleTestDef implements TestDefinition, StructureAware {
	private Log log = LogFactory.getLog(ComplexExampleTestDef.class);

	private List<TestDefinition> tasks;
	private TreeSPath path;

	public void execute(TestRun testRun) {
		log.info("Execute sequence of test definitions...");

		for (TestDefinition task : tasks) {
			task.execute(testRun);

			// Reset current path in case it has been changed
			TestResult result = testRun.getTestResult();
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(null, path);
			}

		}
	}

	public void setTasks(List<TestDefinition> tasks) {
		this.tasks = tasks;
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.path = (TreeSPath) path;

		Integer count = 0;
		for (TestDefinition task : tasks) {
			String description = "";
			if (task instanceof ExampleTask) {
				description = ((ExampleTask) task).getDescription();
			}
			SimpleSElement element = new SimpleSElement(description);
			TreeSPath taskPath = this.path.createChild(count.toString());
			registry.register(taskPath, element);
			if (task instanceof StructureAware) {
				((StructureAware) task).notifyCurrentPath(registry, taskPath);
			}
			count++;
		}
	}

}
