package org.argeo.slc.core.structure;


/**
 * Basic implementation of <code>StructureElement</code>.
 * 
 * @see TreeSPath
 */
public class SimpleSElement implements StructureElement {
	/** For ORM */
	private Long tid;
	private String label;

	/** For ORM */
	public SimpleSElement() {
	}

	/** Constructor */
	public SimpleSElement(String label) {
		this.label = label;
	}

	/** Constructor */
	public SimpleSElement(String label, String defaultLabel) {
		this(label != null ? label : defaultLabel);
	}

	public String getLabel() {
		return label;
	}

	/** Sets the label. */
	public void setLabel(String label) {
		this.label = label;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
