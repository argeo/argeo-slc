package org.argeo.slc.core.structure;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.TreeSelectionModel;

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

	/** Constructor */
	public SimpleSElement(SimpleSElement sElement) {
		setLabel(sElement.getLabel());
		setTags(new TreeMap<String, String>(sElement.getTags()));
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
		return new SimpleSElement(this);
	}

}
