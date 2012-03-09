/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureElementProvider;
import org.argeo.slc.structure.StructureRegistry;
import org.argeo.slc.test.TestDefinition;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestRun;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(TestRun testRun) {
		if (log.isTraceEnabled())
			log.trace("Execute sequence of test definitions...");

		int i = 0;
		for (TestDefinition task : tasks) {
			TestResult result = testRun.getTestResult();
			if (result instanceof StructureAware) {
				((StructureAware) result).notifyCurrentPath(registry,
						taskPaths.get(i));
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

	@SuppressWarnings("unchecked")
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
