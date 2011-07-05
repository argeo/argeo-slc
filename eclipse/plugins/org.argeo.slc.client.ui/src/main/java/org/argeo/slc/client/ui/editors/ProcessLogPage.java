package org.argeo.slc.client.ui.editors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.argeo.slc.execution.ExecutionStep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ProcessLogPage extends FormPage {
	public final static String ID = "processLogPage";

	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	/** Where the log is displayed. */
	private Text text;
	/**
	 * Stores logs received before the text was shown. TODO : rather store in in
	 * JCR and reads it from there.
	 */
	private StringBuffer beforeTextInit = new StringBuffer("");

	public ProcessLogPage(FormEditor editor) {
		super(editor, ID, "Log");
	}

	@Override
	public synchronized void createPartControl(Composite parent) {
		// bypass createFormContent
		FormToolkit tk = getEditor().getToolkit();
		// parent.setLayout(new FillLayout());
		text = tk.createText(parent, "", SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		text.setEditable(false);
		
		// transfer the existing buffer the first time
		if (beforeTextInit.length() > 0) {
			text.append(beforeTextInit.toString());
			// clear buffer
			beforeTextInit.setLength(0);
		}

	}

//	@Override
//	protected synchronized void createFormContent(IManagedForm mf) {
//		ScrolledForm form = mf.getForm();
//		form.setExpandHorizontal(true);
//		form.setExpandVertical(true);
//		// form.setText("Log");
//		FillLayout mainLayout = new FillLayout();
//		form.getBody().setLayout(mainLayout);
//
//		FormToolkit tk = getManagedForm().getToolkit();
//		text = tk.createText(form.getBody(), "", SWT.MULTI | SWT.H_SCROLL
//				| SWT.V_SCROLL);
//		text.setEditable(false);
//		// transfer the existing buffer the first time
//		if (beforeTextInit.length() > 0) {
//			text.append(beforeTextInit.toString());
//			// clear buffer
//			beforeTextInit.setLength(0);
//		}
//	}

	public synchronized void addSteps(List<ExecutionStep> steps) {
		final StringBuffer buf = new StringBuffer("");
		for (ExecutionStep step : steps) {
			buf.append(dateFormat.format(step.getTimestamp()));
			buf.append(' ');
			if (step.getType().equals(ExecutionStep.PHASE_START)) {
				buf.append("## START ").append(step.getLog());
				buf.append('\n');
			} else if (step.getType().equals(ExecutionStep.PHASE_END)) {
				buf.append("## END   ").append(step.getLog());
				buf.append("\n");
			} else {
				buf.append(step.getLog());
			}
		}

		if (text != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					text.append(buf.toString());
				}
			});
		} else
			beforeTextInit.append(buf);
	}

	@Override
	public Control getPartControl() {
		return text;
	}

	@Override
	public void setFocus() {
		if (text != null)
			text.setFocus();
	}

}
