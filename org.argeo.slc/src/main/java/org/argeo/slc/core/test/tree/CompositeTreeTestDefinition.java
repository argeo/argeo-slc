package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestRun;

public class CompositeTreeTestDefinition implements TestDefinition,
		StructureAware {
	private Log log = LogFactory.getLog(CompositeTreeTestDefinition.class);

	private List<TestDefinition> tasks = null;
	private List<TreeSPath> taskPaths = null;
	private TreeSPath path;

	public void execute(TestRun testRun) {
		log.info("Execute sequence of test definitions...");

		int i = 0;
		for (TestDefinition task : tasks) {
			TestResult result = testRun.getTestResult();
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(null, taskPaths
						.get(i));
			}

			task.execute(testRun);

			// Reset current path in case it has been changed
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(null, path);
			}
			i++;
		}
	}

	public void setTasks(List<TestDefinition> tasks) {
		this.tasks = tasks;
		if (tasks != null) {
			taskPaths = new Vector<TreeSPath>();
		}
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		this.path = (TreeSPath) path;

		// clear task paths
		taskPaths.clear();

		Integer count = 0;
		for (TestDefinition task : tasks) {
			final StructureElement element;
			if (task instanceof StructureElement) {
				element = (StructureElement) task;
			} else {
				element = new SimpleSElement("<no desc>");
			}
			TreeSPath taskPath = this.path.createChild(count.toString());
			registry.register(taskPath, element);
			taskPaths.add(taskPath);
			if (task instanceof StructureAware) {
				((StructureAware) task).notifyCurrentPath(registry, taskPath);
			}
			count++;
		}
	}

}
