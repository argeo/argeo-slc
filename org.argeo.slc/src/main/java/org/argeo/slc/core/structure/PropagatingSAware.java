package org.argeo.slc.core.structure;

import java.util.List;

/** Structure aware object able to propagate registration.*/
public interface PropagatingSAware extends StructureAware {
	/**
	 * Adds a structure aware to which registration should be propagated. The
	 * passed object will be registered when this object will be
	 * registered itself, so it should not have been registered before. <b>It doesn't
	 * have to be consistent with the tree structure defined by tree based
	 * registry elements (although it will often make more sense)</b>.
	 */
	public void addToPropagationList(StructureAware sAware);

	/** Returns the list of structure aware to propagate to. */
	public List<StructureAware> getPropagationList();

}
