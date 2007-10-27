package org.argeo.slc.ant.structure;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;

/** Ant task that can be registered within a structure. */
public abstract class SAwareTask extends AbstractSpringTask {
	private final DefaultTreeSAware sAware = new DefaultTreeSAware();
	private final List<AbstractSpringArg> sAwareArgs = new Vector<AbstractSpringArg>();

	@Override
	public void init() throws BuildException {
		StructureRegistry registry = getRegistry();
		Target target = getOwningTarget();

		TreeSPath targetPath = createTargetPath(target);
		TreeSElement targetElement = (TreeSElement) registry
				.getElement(createTargetPath(target));

		if (targetElement == null) {
			targetElement = new TreeSElement(target.getDescription(),
					"<no target desc>");
			registry.register(targetPath, targetElement);
		}

		TreeSElement taskElement = new TreeSElement(getDescription(),
				"<no task desc>");
		sAware.setElement(taskElement);
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
		// init registered args
		for (AbstractSpringArg arg : sAwareArgs) {
			Object obj = arg.getBeanInstance();

			if (obj instanceof StructureAware && sAware != null) {
				StructureAware sAwareT = (StructureAware) obj;
				sAware.addToPropagationList(arg.getBean(), sAwareT);
			}
		}

		// register the task in the structure
		TreeSPath targetPath = createTargetPath(getOwningTarget());
		TreeSPath taskPath = targetPath.createChild(getTaskName()
				+ targetPath.listChildren(getRegistry()).size());
		getRegistry().register(taskPath, sAware);

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
}
