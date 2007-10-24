package org.argeo.slc.ant.structure;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.springframework.util.CollectionUtils;

import org.argeo.slc.ant.SlcProjectHelper;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.core.structure.PropagatingSAware;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSAware;
import org.argeo.slc.core.structure.tree.TreeSElement;

/** Ant types allowing to propagate structure informations.*/
public abstract class SAwareArg extends AbstractSpringArg{
	//private TreeSAware parentSAware;

	@Override
	protected Object getBeanInstance() {
		Object obj = super.getBeanInstance();
/*
		if (obj instanceof TreeSAware && parentSAware != null) {
			TreeSAware sAware = (TreeSAware) obj;
			TreeSElement parentElement = (TreeSElement) parentSAware
					.getElement();
			String name = getBean() + parentElement.getChildren().size();
			TreeSElement element = parentElement.createChild(name,
					getDescription() != null ? getDescription()
							: "<no bean desc>");
			sAware.setElement(element);

			parentSAware.addToPropagationList(sAware);
//			StructureRegistry registry = (StructureRegistry) getProject()
//					.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
//			registry.register(parentSAware);
		}
		*/
		return obj;
	}

	public void init(TreeSAware parentSAware){
		
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

			parentSAware.addToPropagationList(sAware);
//			StructureRegistry registry = (StructureRegistry) getProject()
//					.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
//			registry.register(parentSAware);
		}
	}
	
//	public void setParentSAware(TreeSAware parentSAware) {
//		this.parentSAware = parentSAware;
//	}


	public StructureElement getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onRegister(StructureRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	
}
