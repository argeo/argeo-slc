package org.argeo.slc.client.ui.dist.model;

/** Common super class for all tree elements of the Distributions View*/
public abstract class DistParentElem {
	public final static Character VERSION_SEP = '-';

	private boolean inHome = false;
	private boolean isReadOnly = false;

	public DistParentElem(boolean inHome, boolean isReadOnly) {
		this.inHome = inHome;
		this.isReadOnly = isReadOnly;
	}

	public DistParentElem() {
	}

	public abstract String getLabel();

	public abstract Object[] getChildren();

	public boolean hasChildren() {
		return true;
	}

	public void dispose() {
	}

	public void setInHome(boolean inHome) {
		this.inHome = inHome;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean inHome() {
		return inHome;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}
}
