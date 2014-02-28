package org.argeo.slc.client.ui.dist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.argeo.eclipse.ui.TreeParent;

/** Common super class for all tree elements of the Distributions View */
public abstract class DistParentElem extends TreeParent {
	protected final static Character VERSION_SEP = '-';

	protected static final List<String> ARGEO_SYSTEM_WKSP;
	static {
		List<String> tmpList = new ArrayList<String>();
		tmpList.add("main");
		tmpList.add("proxy");
		tmpList.add("security");
		tmpList.add("localrepo");
		ARGEO_SYSTEM_WKSP = Collections.unmodifiableList(tmpList);
	}

	private boolean inHome = false;
	private boolean isReadOnly = false;

	public DistParentElem(String name, boolean inHome, boolean isReadOnly) {
		super(name);
		this.inHome = inHome;
		this.isReadOnly = isReadOnly;
	}

	public DistParentElem(String name) {
		super(name);
	}

	// public abstract String getLabel();
	//
	// public abstract Object[] getChildren();
	//
	// public boolean hasChildren() {
	// return true;
	// }
	//
	// public void dispose() {
	// }

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
