package org.argeo.slc.core.structure;


/** Structure aware object in which the wrapped element can be externally set. */
public interface WritableSAware extends StructureAware {
	/** Sets the wrapped element. */
	public void setElement(StructureElement element);

}
