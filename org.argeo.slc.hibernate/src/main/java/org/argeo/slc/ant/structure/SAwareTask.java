package org.argeo.slc.ant.structure;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

import org.argeo.slc.ant.SlcAntException;
import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;

/** Ant task that can be registered within a structure. */
public abstract class SAwareTask extends AbstractSpringTask {
	private String path;
	private TreeSPath treeSPath;
	private final List<AbstractSpringArg> sAwareArgs = new Vector<AbstractSpringArg>();

	private StructureElementArg structureElementArg;

	@Override
	public void init() throws BuildException {
		StructureRegistry<TreeSPath> registry = getRegistry();
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

	/**
	 * Includes this arg in the checks for propagation of structure related
	 * information.
	 */
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
		if (path == null) {
			// register the task in the structure
			TreeSPath targetPath = createTargetPath(getOwningTarget());
			TreeSPath taskPath = targetPath.createChild(getTaskName()
					+ targetPath.listChildren(getRegistry()).size());

			treeSPath = taskPath;
		} else {
			treeSPath = new TreeSPath(path);
		}

		if (getRegistry().getElement(treeSPath) == null) {
			// No structure element registered.
			if (structureElementArg != null) {
				getRegistry().register(treeSPath,
						structureElementArg.getStructureElement());
			} else {
				if (getDescription() != null) {
					getRegistry().register(treeSPath,
							new SimpleSElement(getDescription()));
				}
			}
		}

		// notify registered args
		for (AbstractSpringArg arg : sAwareArgs) {
			Object obj = arg.getBeanInstance();

			if (obj instanceof StructureAware) {
				StructureAware<TreeSPath> sAwareT = (StructureAware<TreeSPath>) obj;
				sAwareT.notifyCurrentPath(getRegistry(), treeSPath);
			}
		}

		// execute depending on the registry mode
		String mode = getRegistry().getMode();
		if (mode.equals(StructureRegistry.ALL)) {
			executeActions(mode);
		} else if (mode.equals(StructureRegistry.ACTIVE)) {
			List<TreeSPath> activePaths = getRegistry().getActivePaths();

			if (activePaths.contains(treeSPath)) {
				if (activePaths.contains(treeSPath)) {
					executeActions(mode);
				}
			}
		}

	}

	/** Actions to be executed by the implementor. */
	protected abstract void executeActions(String mode);

	/** Create a reference to an external structure element. */
	public StructureElementArg createStructureElement() {
		if (structureElementArg != null)
			throw new SlcAntException("Arg already set.");
		structureElementArg = new StructureElementArg();
		return structureElementArg;
	}

	/** Gets the underlying structure registry. */
	protected StructureRegistry<TreeSPath> getRegistry() {
		return (StructureRegistry<TreeSPath>) getProject().getReference(
				SlcProjectHelper.REF_STRUCTURE_REGISTRY);
	}

	/** Creates the treeSPath for a given Ant target. */
	protected static TreeSPath createTargetPath(Target target) {
		TreeSPath projectPath = (TreeSPath) target.getProject().getReference(
				SlcProjectHelper.REF_PROJECT_PATH);
		return projectPath.createChild(target.getName());
	}

	/** Gets the treeSPath under which this task is registered. */
	public TreeSPath getTreeSPath() {
		return treeSPath;
	}

	public String getLabel() {
		String description = super.getDescription();
		if (description == null) {
			return "<no task def>";
		} else {
			return description;
		}
	}

	public void setPath(String path) {
		this.path = path;
	}
}

class StructureElementArg extends AbstractSpringArg {
	public StructureElement getStructureElement() {
		return (StructureElement) getBeanInstance();
	}
}