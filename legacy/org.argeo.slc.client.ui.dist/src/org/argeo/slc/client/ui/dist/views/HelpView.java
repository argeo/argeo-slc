package org.argeo.slc.client.ui.dist.views;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Display some info about the distribution */
public class HelpView extends ViewPart {
	public final static String ID = DistPlugin.PLUGIN_ID + ".helpView";

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		Browser browser = new Browser(parent, SWT.NONE);
		browser.setUrl("/repo/howto.html");
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	/** Force refresh of the whole view */
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

}