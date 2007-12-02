package org.argeo.slc.core.structure;


/**
 * Basic implementation of <code>StructureElement</code>.
 * 
 * @see TreeSPath
 */
public class SimpleSElement implements StructureElement {
	/** For ORM */
	private Long tid;
	private String description;

	/** For ORM */
	public SimpleSElement() {
	}

	/** Constructor */
	public SimpleSElement(String description) {
		this.description = description;
	}

	/** Constructor */
	public SimpleSElement(String description, String defaultDescription) {
		this(description != null ? description : defaultDescription);
	}

	public String getDescription() {
		return description;
	}

	/** Sets the description. */
	public void setDescription(String description) {
		this.description = description;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
