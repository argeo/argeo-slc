package org.argeo.slc.ant.structure;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;

/** Ant task that can be registered within a structure. */
public abstract class SAwareTask extends AbstractSpringTask implements
		StructureElement {
	private TreeSPath path;
	private final List<AbstractSpringArg> sAwareArgs = new Vector<AbstractSpringArg>();

	@Override
	public void init() throws BuildException {
		StructureRegistry registry = getRegistry();
		Target target = getOwningTarget();

		TreeSPath targetPath = createTargetPath(target);
		SimpleSElement targetElement = (SimpleSElement) registry
				.getElement(createTargetPath(target));

		if (targetElement == null) {
			targetElement = new SimpleSElement(target.getDescription(),
					"<no target desc>");
			registry.register(targetPath, targetElement);
		}
	}

	protected void addSAwareArg(AbstractSpringArg arg) {
		sAwareArgs.add(arg);
	}

	@Override
	/**
	 * Called by Ant at runtime. Decides whether to call the actions depending
	 * of the mode of the underlying structure registry.
	 * 
	 * @see #executeActions
	 * @see StructureRegistry
	 */
	public final void execute() throws BuildException {
		// register the task in the structure
		TreeSPath targetPath = createTargetPath(getOwningTarget());
		TreeSPath taskPath = targetPath.createChild(getTaskName()
				+ targetPath.listChildren(getRegistry()).size());
		getRegistry().register(taskPath, this);
		path = taskPath;

		// notify registered args
		for (AbstractSpringArg arg : sAwareArgs) {
			Object obj = arg.getBeanInstance();

			if (obj instanceof StructureAware) {
				StructureAware sAwareT = (StructureAware) obj;
				sAwareT.notifyCurrentPath(getRegistry(), taskPath);
			}
		}

		// execute depending on the registry mode
		String mode = getRegistry().getMode();
		if (mode.equals(StructureRegistry.ALL)) {
			executeActions(mode);
		} else if (mode.equals(StructureRegistry.ACTIVE)) {
			List<StructurePath> activePaths = getRegistry().getActivePaths();

			if (activePaths.contains(targetPath)) {
				if (activePaths.contains(taskPath)) {
					executeActions(mode);
				}
			}
		}

	}

	/** Actions to be executed by the implementor. */
	protected abstract void executeActions(String mode);

	/** Gets the underlying structure registry. */
	protected StructureRegistry getRegistry() {
		return (StructureRegistry) getProject().getReference(
				SlcProjectHelper.REF_STRUCTURE_REGISTRY);
	}

	/** Creates the path for a given Ant target. */
	protected static TreeSPath createTargetPath(Target target) {
		TreeSPath projectPath = SlcProjectHelper.getProjectPath(target
				.getProject());
		return projectPath.createChild(target.getName());
	}

	public TreeSPath getPath() {
		return path;
	}

	@Override
	public String getDescription() {
		String description = super.getDescription();
		if (description == null) {
			return "<no task def>";
		} else {
			return description;
		}
	}

}
