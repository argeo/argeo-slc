package org.argeo.slc.ant.structure;

import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.ant.spring.AbstractSpringTask;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;

/** Ant task that can be registered within a structure. */
public abstract class SAwareTask extends AbstractSpringTask {
	protected final TreeSAware sAware = new DefaultTreeSAware();
	protected final List<SAwareArg> sAwareArgs = new Vector<SAwareArg>();

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
		for(SAwareArg arg : sAwareArgs){
			arg.init(sAware);
		}
		
		getRegistry().register(sAware);
		
		String mode = getRegistry().getMode();
		if (mode.equals(StructureRegistry.ALL)) {
			executeActions(mode);
		} else if (mode.equals(StructureRegistry.ACTIVE)) {
			List<StructurePath> activePaths = getRegistry().getActivePaths();
			
			StructurePath targetPath = createTargetPath(getOwningTarget());
			if(activePaths.contains(targetPath)){
				if (activePaths.contains(sAware.getElement().getPath())) {
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
	protected static StructurePath createTargetPath(Target target) {
		TreeSPath projectPath = SlcProjectHelper.getProjectPath(target
				.getProject());
		return TreeSPath.createChild(projectPath, target.getName());
	}
}
