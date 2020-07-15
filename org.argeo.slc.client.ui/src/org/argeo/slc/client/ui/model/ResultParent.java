package org.argeo.slc.client.ui.model;

import org.argeo.eclipse.ui.TreeParent;

/**
 * Common base UI object to build result Tree.
 */

public abstract class ResultParent extends TreeParent {

	public ResultParent(String name) {
		super(name);
	}

	private boolean isPassed = true;

	protected synchronized void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}

	public boolean isPassed() {
		return isPassed;
	}

	@Override
	public synchronized boolean hasChildren() {
		// only initialize when needed : correctly called by the jface framework
		if (!isLoaded())
			initialize();
		return super.hasChildren();
	}

	public void forceFullRefresh() {
		// if (isDisposed)
		// return;
		if (hasChildren())
			clearChildren();
		initialize();
	}

	protected abstract void initialize();
}
