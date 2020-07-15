package org.argeo.cms.ui.workbench.jcr;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.jcr.parts.AbstractJcrQueryEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/** Enables end user to type and execute any JCR query. */
public class GenericJcrQueryEditor extends AbstractJcrQueryEditor {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".genericJcrQueryEditor";

	private Text queryField;

	@Override
	public void createQueryForm(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		queryField = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		queryField.setText(initialQuery);
		queryField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button execute = new Button(parent, SWT.PUSH);
		execute.setText("Execute");

		Listener executeListener = new Listener() {
			private static final long serialVersionUID = -918256291554301699L;

			public void handleEvent(Event event) {
				executeQuery(queryField.getText());
			}
		};

		execute.addListener(SWT.Selection, executeListener);
		// queryField.addListener(SWT.DefaultSelection, executeListener);
	}

	@Override
	public void setFocus() {
		queryField.setFocus();
	}
}
