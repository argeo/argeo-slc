package org.argeo.slc.ide.ui.launch.preferences;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SlcLaunchPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public final static String PREF_SLC_RUNTIME_LOCATION = SlcIdeUiPlugin.ID
			+ ".slcRuntimeLocation";
	public final static String PREF_EMBEDDED_JAVA_LIBRARY_PATH = SlcIdeUiPlugin.ID
			+ ".embeddedJavaLibraryPath";

	public SlcLaunchPreferencePage() {
//		IPreferenceStore store = SlcUiLaunchPlugin.getDefault()
//				.getPreferenceStore();
//		setPreferenceStore(store);
		setDescription("Argeo SLC Launch Preferences");
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(PREF_SLC_RUNTIME_LOCATION,
				"SLC Runtime", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PREF_EMBEDDED_JAVA_LIBRARY_PATH,
				"Embedded Java Library Path", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
