package org.argeo.slc.ant;

import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;

public abstract class SAwareTask extends AbstractSpringTask {
	protected final TreeSAware sAware = new DefaultTreeSAware();

	@Override
	public void init() throws BuildException {
		StructureRegistry registry = getRegistry();
		Target target = getOwningTarget();
		TreeSElement projectElement = (TreeSElement) registry
				.getElement(SlcProjectHelper.getProjectPath(getProject()));
		TreeSElement targetElement = (TreeSElement) registry
				.getElement(createTargetPath(target));

		if (targetElement == null) {
			// create target element
			targetElement = projectElement.createChild(target.getName(), target
					.getDescription() != null ? target.getDescription()
					: "<no target>");
			registry.register(targetElement);
		}

		TreeSElement taskElement = targetElement.createChild(getTaskName()
				+ targetElement.getChildren().size(),
				getDescription() != null ? getDescription() : "<no task desc>");
		sAware.setElement(taskElement);
		registry.register(sAware);
	}

	@Override
	public final void execute() throws BuildException {
		String mode = getRegistry().getMode();
		if (mode.equals(StructureRegistry.ALL)) {
			executeActions(mode);
		} else if (mode.equals(StructureRegistry.ACTIVE)) {
			List<StructurePath> activePaths = getRegistry().getActivePaths();
			if (activePaths.contains(sAware.getElement().getPath())) {
				executeActions(mode);
			}
		}

	}

	protected abstract void executeActions(String mode);

	protected StructureRegistry getRegistry() {
		return (StructureRegistry) getProject().getReference(
				SlcProjectHelper.REF_STRUCTURE_REGISTRY);
	}

	protected static StructurePath createTargetPath(Target target) {
		TreeSPath projectPath = SlcProjectHelper.getProjectPath(target
				.getProject());
		return TreeSPath.createChild(projectPath, target.getName());
	}
}
