package org.argeo.swt.desktop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** A minimalistic web browser based on {@link Browser}. */
public class MiniBrowser {
	private Browser browser;
	private Text addressT;

	public MiniBrowser(Composite composite, String url) {
		createUi(composite);
		setUrl(url);
	}

	public Control createUi(Composite parent) {
		parent.setLayout(noSpaceGridLayout(new GridLayout()));
		Control toolBar = createToolBar(parent);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Control body = createBody(parent);
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return body;
	}

	public Control createToolBar(Composite parent) {
		Composite toolBar = new Composite(parent, SWT.NONE);
		toolBar.setLayout(new FillLayout());
		addressT = new Text(toolBar, SWT.SINGLE);
		addressT.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setUrl(addressT.getText().trim());
			}
		});
		return toolBar;
	}

	public Control createBody(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				addressT.setText(event.location);
			}

		});
		browser.addTitleListener(e -> titleChanged(e.title));
		return browser;
	}

	void setUrl(String url) {
		if (browser != null && url != null && !url.equals(browser.getUrl()))
			browser.setUrl(url.toString());
	}

	/** Called when URL changed; to be overridden, does nothing by default. */
	protected void urlChanged(String url) {
	}

	/** Called when title changed; to be overridden, does nothing by default. */
	protected void titleChanged(String title) {
	}

	private static GridLayout noSpaceGridLayout(GridLayout layout) {
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}

	public static void main(String[] args) {
		Display display = Display.getCurrent() == null ? new Display() : Display.getCurrent();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);

		String url = args.length > 0 ? args[0] : "https://duckduckgo.com/";
		new MiniBrowser(shell, url) {

			@Override
			protected void titleChanged(String title) {
				shell.setText(title);
			}
		};
		shell.open();
		shell.setSize(new Point(800, 480));

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
