package org.argeo.slc.ui.launch.script;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SlcScriptLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab {

	private Text scriptL;
	private Text propertiesTF;
	private Text runtimeTF;
	private Text targetsTF;
	private Button pre093B;

	public void createControl(Composite parent) {
		Composite body = new Composite(parent, SWT.NONE);
		setControl(body);
		body.setLayout(new GridLayout(1, false));
		body.setFont(parent.getFont());

		createLabel(body, "Script location");
		scriptL = createSingleText(body);
		scriptL.setEditable(false);
		scriptL.setBackground(body.getBackground());

		createLabel(body, "Runtime");
		runtimeTF = createSingleText(body);

		createLabel(body, "Targets");
		targetsTF = createSingleText(body);

		createLabel(body, "Properties");
		propertiesTF = createMultipleText(body, 10);

		pre093B = createCheckBox(body, "Pre SLC v0.9.3");
	}

	public String getName() {
		return "SLC";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			IFile script = (IFile) configuration.getMappedResources()[0];
			scriptL.setText(script.getLocation().toFile().getAbsolutePath());

			boolean pre093 = configuration.getAttribute(
					SlcScriptLaunchDelegate.ATTR_PRE093, false);

			propertiesTF.setText(configuration.getAttribute(
					SlcScriptLaunchDelegate.ATTR_PROPERTIES, ""));
			runtimeTF.setText(configuration.getAttribute(
					SlcScriptLaunchDelegate.ATTR_RUNTIME, ""));
			targetsTF.setText(configuration.getAttribute(
					SlcScriptLaunchDelegate.ATTR_TARGETS, ""));
			pre093B.setSelection(pre093);
		} catch (CoreException e) {
			throw new RuntimeException("Cannot initialize tab", e);
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(SlcScriptLaunchDelegate.ATTR_PROPERTIES,
				propertiesTF.getText());
		configuration.setAttribute(SlcScriptLaunchDelegate.ATTR_RUNTIME,
				runtimeTF.getText());
		configuration.setAttribute(SlcScriptLaunchDelegate.ATTR_TARGETS,
				targetsTF.getText());
		configuration.setAttribute(SlcScriptLaunchDelegate.ATTR_PRE093, pre093B
				.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	// UI Utils
	protected Label createLabel(Composite parent, String text) {
		Label t = new Label(parent, SWT.NONE | SWT.WRAP);
		t.setText(text);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		t.setLayoutData(gd);
		return t;
	}

	protected Text createSingleText(Composite parent) {
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		t.setLayoutData(gd);
		t.addModifyListener(modifyListener);
		return t;
	}

	protected Text createMultipleText(Composite parent, int verticalSpan) {
		Text t = new Text(parent, SWT.MULTI | SWT.BORDER);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.horizontalSpan = 1;
		gd.verticalSpan = verticalSpan;
		t.setLayoutData(gd);
		t.addModifyListener(modifyListener);
		return t;
	}

	protected Button createCheckBox(Composite parent, String label) {
		Button b = new Button(parent, SWT.CHECK);
		b.setFont(parent.getFont());
		b.setText(label);
		b.addSelectionListener(selectionListener);
		return b;

	}

	// LISTENERS
	/**
	 * Modify listener that simply updates the owning launch configuration
	 * dialog.
	 */
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	};
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	};

}
