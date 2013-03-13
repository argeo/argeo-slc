package org.argeo.slc.client.ui.dist.model;

public abstract class DistParentElem {
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

	public void dispose() {
	}

	public boolean inHome() {
		return inHome;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}
}
