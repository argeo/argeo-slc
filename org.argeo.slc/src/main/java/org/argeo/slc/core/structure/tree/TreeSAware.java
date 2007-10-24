package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.PropagatingSAware;
import org.argeo.slc.core.structure.WritableSAware;

/**
 * Provides methods to externally propagate tree related informations in tree
 * based registries.
 * 
 * @see TreeSElement
 */
public interface TreeSAware extends WritableSAware, PropagatingSAware {

}
