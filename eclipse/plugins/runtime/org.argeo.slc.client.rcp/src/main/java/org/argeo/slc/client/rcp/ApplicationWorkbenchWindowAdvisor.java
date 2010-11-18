package org.argeo.slc.client.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Set here initial default size of the UI
 * 
 * @author bsinou
 * 
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private TrayItem trayItem;

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.getWorkbenchConfigurer().restoreState();
		// set default window size
		configurer.setInitialSize(new Point(1200, 900));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(true);

	}

	public void postWindowOpen() {
		initTray();
	}

	@Override
	public boolean preWindowShellClose() {
		// hide but do not dispose if tray is supported
		if (trayItem != null) {
			getWindowConfigurer().getWindow().getShell().setVisible(false);
			return false;
		} else
			return true;
	}

	/** Init tray support */
	protected void initTray() {
		IWorkbenchWindow window = getWindowConfigurer().getWindow();
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		if (trayItem == null)
			return;

		// image
		Image trayImage = ClientRcpPlugin.getDefault().getImageRegistry()
				.get("argeoTrayIcon");
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("Argeo SLC");

		// add pop-menu
		// TODO: contribute more commands
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				IWorkbenchWindow window = getWindowConfigurer().getWindow();
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);
				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						getWindowConfigurer().getWorkbenchConfigurer()
								.getWorkbench().close();
					}
				});
				menu.setVisible(true);
			}
		});

		// add behavior when clicked upon
		trayItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Shell shell = getWindowConfigurer().getWindow().getShell();
				if (shell.isVisible()) {
					if (shell.getMinimized())
						shell.setMinimized(false);
					else {
						shell.setVisible(false);
						shell.setMinimized(true);
					}
				} else {
					shell.setVisible(true);
					shell.setActive();
					shell.setFocus();
					shell.setMinimized(false);
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (trayItem != null)
			trayItem.dispose();
	}

}
