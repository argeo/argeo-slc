package org.argeo.slc.ant;

import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;

public abstract class SAwareArg extends AbstractSpringArg {
	private TreeSAware parentSAware;

	@Override
	protected Object getBeanInstance() {
		Object obj = super.getBeanInstance();

		if (obj instanceof TreeSAware && parentSAware != null) {
			TreeSAware sAware = (TreeSAware) obj;
			TreeSElement parentElement = (TreeSElement) parentSAware
					.getElement();
			String name = getBean() + parentElement.getChildren().size();
			TreeSElement element = parentElement.createChild(name,
					getDescription() != null ? getDescription()
							: "<no bean desc>");
			sAware.setElement(element);

			parentSAware.addChild(sAware);
			StructureRegistry registry = (StructureRegistry) getProject()
					.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
			registry.register(sAware);
		}
		return obj;
	}

	public void setParentSAware(TreeSAware parentSAware) {
		this.parentSAware = parentSAware;
	}

}
