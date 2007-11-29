package org.argeo.slc.ui.launch.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.argeo.slc.ui.launch.SlcUiLaunchPlugin;

public class SlcPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public final static String PREF_SLC_RUNTIME_LOCATION = SlcUiLaunchPlugin.ID
			+ ".slcRuntimeLocation";

	public SlcPreferencePage() {
		IPreferenceStore store = SlcUiLaunchPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
		setDescription("Argeo SLC Preferences");
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(PREF_SLC_RUNTIME_LOCATION,
				"SLC Runtime", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
