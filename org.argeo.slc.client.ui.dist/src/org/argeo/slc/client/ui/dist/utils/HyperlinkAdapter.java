package org.argeo.slc.client.ui.dist.utils;

import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/** Convenience class to reduce the number of methods to implement */
public abstract class HyperlinkAdapter implements IHyperlinkListener {

	public void linkEntered(HyperlinkEvent e) {
	}

	public void linkExited(HyperlinkEvent e) {
	}

	/** Must be overriden **/
	public abstract void linkActivated(HyperlinkEvent e);
}
