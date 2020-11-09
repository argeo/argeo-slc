package org.argeo.slc.client.rcp;

import org.argeo.cms.ui.workbench.rcp.SecureWorkbenchWindowAdvisor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.part.EditorInputTransfer;

/**
 * Custom {@link SecureWorkbenchWindowAdvisor} which can add a tray. TODO: to
 * be factorized in Argeo Commons and made more generic.
 */
public class SlcSecureWorkbenchWindowAdvisor extends
		SecureWorkbenchWindowAdvisor {
	public final static String IN_TRAY_PROPERTY = "org.argeo.slc.ui.inTray";

	private TrayItem trayItem;

	public SlcSecureWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer, String username) {
		super(configurer, username);
	}

	public void postWindowOpen() {
		String inTray = System.getProperty(IN_TRAY_PROPERTY);
		if (inTray != null && inTray.equals("true")) {
			initTray();
		}
	}

	@Override
	public void preWindowOpen() {
		getWindowConfigurer().addEditorAreaTransfer(
				EditorInputTransfer.getInstance());
		getWindowConfigurer().configureEditorAreaDropListener(
				new DropTargetAdapter() {

					@Override
					public void dragEnter(DropTargetEvent event) {
						System.out.println("DROP enter!!! " + event);
					}

					@Override
					public void dragLeave(DropTargetEvent event) {
						System.out.println("DROP leave!!! " + event);
					}

					public void drop(DropTargetEvent event) {
						System.out.println("DROP drop!!! " + event);

					}

					@Override
					public void dropAccept(DropTargetEvent event) {
						System.out.println("DROP accept!!! " + event);
						super.dropAccept(event);
					}

				});
		super.preWindowOpen();
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
		Shell shell = window.getShell();
		final Tray tray = shell.getDisplay().getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		if (trayItem == null)
			return;

		// image
		Image trayImage = SlcRcpPlugin.getDefault().getImageRegistry()
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

		// start hidden
		// shell.setVisible(false);
	}

	@Override
	public void dispose() {
		if (trayItem != null)
			trayItem.dispose();
	}

}
