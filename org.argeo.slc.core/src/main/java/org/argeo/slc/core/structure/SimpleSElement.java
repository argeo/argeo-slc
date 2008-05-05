package org.argeo.slc.core.structure;

import java.util.Map;
import java.util.TreeMap;

/**
 * Basic implementation of <code>StructureElement</code>.
 * 
 * @see TreeSPath
 */
public class SimpleSElement implements StructureElement {
	/** For ORM */
	private Long tid;
	private String label;
	private Map<String, String> tags = new TreeMap<String, String>();

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

	public Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public SimpleSElement clone(){
		SimpleSElement clone = new SimpleSElement();
		clone.setLabel(getLabel());
		clone.setTags(getTags());
		return clone;
	}

}
