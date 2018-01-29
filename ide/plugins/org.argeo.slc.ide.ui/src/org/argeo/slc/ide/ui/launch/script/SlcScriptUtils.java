package org.argeo.slc.ide.ui.launch.script;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.variables.VariablesPlugin;

public class SlcScriptUtils {

	public final static String ATTR_SCRIPT = "script";
	public final static String ATTR_PROPERTIES = "properties";
	public final static String ATTR_RUNTIME = "runtime";
	public final static String ATTR_TARGETS = "targets";
	public final static String ATTR_PRE093 = "pre093";

	public static String convertToWorkspaceLocation(IResource resource) {
		return VariablesPlugin.getDefault().getStringVariableManager()
				.generateVariableExpression("workspace_loc",
						resource.getFullPath().toString());
	}

	private SlcScriptUtils() {

	}
}
