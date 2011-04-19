package org.argeo.slc.client.ui.editors;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class ProcessLogPage extends FormPage {
	public final static String ID = "processLogrPage";

	public ProcessLogPage(FormEditor editor) {
		super(editor, ID, "Log");
	}

}
