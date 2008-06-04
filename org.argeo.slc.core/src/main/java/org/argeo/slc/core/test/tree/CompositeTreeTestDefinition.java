package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureElementProvider;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestRun;

/**
 * Collection of test definitions propagating tree structure information to its
 * children.
 */
public class CompositeTreeTestDefinition implements TestDefinition,
		StructureAware<TreeSPath> {
	private Log log = LogFactory.getLog(CompositeTreeTestDefinition.class);

	private List<TestDefinition> tasks = null;
	private List<TreeSPath> taskPaths = null;
	private TreeSPath path;
	private StructureRegistry<TreeSPath> registry;

	public void execute(TestRun testRun) {
		log.info("Execute sequence of test definitions...");

		int i = 0;
		for (TestDefinition task : tasks) {
			TestResult result = testRun.getTestResult();
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(registry, taskPaths
						.get(i));
			}

			task.execute(testRun);

			// Reset current path in case it has been changed
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(registry, path);
			}
			i++;
		}
	}

	/** Sets the list of children test definitions */
	public void setTasks(List<TestDefinition> tasks) {
		this.tasks = tasks;
		if (tasks != null) {
			taskPaths = new Vector<TreeSPath>();
		}
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		this.path = path;
		this.registry = registry;

		// clear task paths
		taskPaths.clear();

		Integer count = 0;
		for (TestDefinition task : tasks) {
			final StructureElement element;
			if (task instanceof StructureElementProvider) {
				element = ((StructureElementProvider) task)
						.createStructureElement();
			} else {
				element = new SimpleSElement("[no desc]");
			}
			TreeSPath taskPath = this.path.createChild(count.toString());
			registry.register(taskPath, element);
			taskPaths.add(taskPath);
			if (task instanceof StructureAware) {
				((StructureAware<TreeSPath>) task).notifyCurrentPath(registry,
						taskPath);
			}
			count++;
		}
	}

}
