package org.argeo.slc.client.rap;

import org.argeo.security.ui.rap.RapSecureWorkbenchWindowAdvisor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * Configures the initial size and appearance of a workbench window.
 */
public class SlcRapSecureWorkbenchWindowAdvisor extends
		RapSecureWorkbenchWindowAdvisor {

	public SlcRapSecureWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	// Customisation of the main frame can be done here.
	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		//
		// IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setShowCoolBar(true);
		// configurer.setShowMenuBar(true);
		// configurer.setShowStatusLine(false);
		// configurer.setShowPerspectiveBar(true);
		//		configurer.setTitle("Argeo Secure UI"); //$NON-NLS-1$
		// // Full screen, see
		// //
		// http://dev.eclipse.org/newslists/news.eclipse.technology.rap/msg02697.html
		// configurer.setShellStyle(SWT.NONE);
		// Rectangle bounds = Display.getDefault().getBounds();
		// configurer.setInitialSize(new Point(bounds.width, bounds.height));
	}

}
