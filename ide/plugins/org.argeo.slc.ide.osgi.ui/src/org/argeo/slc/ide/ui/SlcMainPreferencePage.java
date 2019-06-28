package org.argeo.slc.ide.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SlcMainPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public SlcMainPreferencePage() {
//		IPreferenceStore store = SlcIdePlugin.getDefault().getPreferenceStore();
//		setPreferenceStore(store);
		setDescription("Argeo SLC Preferences");
	}

	@Override
	protected void createFieldEditors() {
//		addField(new DirectoryFieldEditor(PREF_SLC_RUNTIME_LOCATION,
//				"SLC Runtime", getFieldEditorParent()));
//		addField(new DirectoryFieldEditor(PREF_EMBEDDED_JAVA_LIBRARY_PATH,
//				"Embedded Java Library Path", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
