package org.argeo.slc.ant;

import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.core.structure.tree.DefaultTreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;

public abstract class SAwareArg extends AbstractSpringArg {
	private DefaultTreeSAware parentSAware;

	@Override
	protected Object getBeanInstance() {
		Object obj = super.getBeanInstance();

		if (obj instanceof DefaultTreeSAware && parentSAware!=null) {
			DefaultTreeSAware sAware = (DefaultTreeSAware) obj;
			TreeSElement parentElement = (TreeSElement) parentSAware
					.getElement();
			String name = getBean() + parentElement.getChildren().size();
			TreeSElement element = parentElement.createChild(name,
					getDescription() != null ? getDescription()
							: "<no bean desc>");
			sAware.setElement(element);
			
			parentSAware.addChild(sAware);
		}
		return obj;
	}

	public void setParentSAware(DefaultTreeSAware parentSAware) {
		this.parentSAware = parentSAware;
	}

	
}
