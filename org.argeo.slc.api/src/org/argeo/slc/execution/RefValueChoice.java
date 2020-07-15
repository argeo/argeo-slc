package org.argeo.slc.execution;

import java.io.Serializable;

/** A choice of ref value to be shown to the end user. */
public class RefValueChoice implements Serializable {
	private static final long serialVersionUID = -1133645722307507774L;
	private String name;
	private String description;

	public RefValueChoice() {
	}

	public RefValueChoice(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
