package org.argeo.slc.ui.launch.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.argeo.slc.ui.launch.SlcUiLaunchPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.swt.widgets.Shell;

public class SlcLaunchShortcut extends OSGiLaunchShortcut {
	private Boolean debug = false;

	private String springOsgiExtenderId = "org.springframework.osgi.extender";
	// private String slcAgentId = "org.argeo.slc.agent";
	// private String osgiBootId = "org.argeo.slc.osgiboot";

	private ISelection selection = null;
	private StringBuffer name = null;

	private final List<String> defaultBundlesToStart = new ArrayList<String>();
	private List<String> bundlesToStart = new ArrayList<String>();

	public SlcLaunchShortcut() {
		super();
		defaultBundlesToStart.add(springOsgiExtenderId);
		// defaultBundlesToStart.add(slcAgentId);
	}

	@Override
	public void launch(ISelection selection, String mode) {
		this.selection = selection;
		this.name = new StringBuffer();

		bundlesToStart = new ArrayList<String>();
		bundlesToStart.addAll(defaultBundlesToStart);
		// Evaluate selection
		if (selection != null) {
			addSelectedProjects(bundlesToStart);
		}

		super.launch(selection, mode);

		// Reset
		this.selection = null;
		this.name = null;
		bundlesToStart = null;
	}

	@Override
	protected void initializeConfiguration(
			ILaunchConfigurationWorkingCopy configuration) {
		try {
			super.initializeConfiguration(configuration);

			// Convert bundle lists
			String targetBundles = configuration.getAttribute(
					IPDELauncherConstants.TARGET_BUNDLES, "");
			configuration.setAttribute(IPDELauncherConstants.TARGET_BUNDLES,
					convertBundleList(bundlesToStart, targetBundles));

			String wkSpaceBundles = configuration.getAttribute(
					IPDELauncherConstants.WORKSPACE_BUNDLES, "");
			configuration.setAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES,
					convertBundleList(bundlesToStart, wkSpaceBundles));

			// Update other default information
			configuration.setAttribute(
					IPDELauncherConstants.DEFAULT_AUTO_START, false);

		} catch (CoreException e) {
			Shell shell = SlcUiLaunchPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot execute SLC launch shortcut", e.getStatus());
		}

	}

	protected String convertBundleList(List<String> bundlesToStart,
			String original) {
		StringBuffer bufBundles = new StringBuffer(1024);
		StringTokenizer stComa = new StringTokenizer(original, ",");
		boolean first = true;
		while (stComa.hasMoreTokens()) {
			if (first)
				first = false;
			else
				bufBundles.append(',');

			String tkComa = stComa.nextToken();
			int indexAt = tkComa.indexOf('@');
			boolean modified = false;
			if (indexAt >= 0) {
				String bundelId = tkComa.substring(0, indexAt);

				if (bundlesToStart.contains(bundelId)) {
					bufBundles.append(bundelId).append('@').append(
							"default:true");
					modified = true;
					if (debug)
						System.out.println("Will start " + bundelId);
				}
			}

			if (!modified)
				bufBundles.append(tkComa);
		}
		String output = bufBundles.toString();
		return output;
	}

	protected void addSelectedProjects(List<String> bundlesToStart) {
		Assert.isNotNull(selection);

		Map<String, IPluginModelBase> bundleProjects = new HashMap<String, IPluginModelBase>();
		for (IPluginModelBase modelBase : PluginRegistry.getWorkspaceModels()) {
			IProject bundleProject = modelBase.getUnderlyingResource()
					.getProject();
			bundleProjects.put(bundleProject.getName(), modelBase);
		}

		IStructuredSelection sSelection = (IStructuredSelection) selection;
		for (Iterator<?> it = sSelection.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof IProject) {
				IProject project = (IProject) obj;
				if (bundleProjects.containsKey(project.getName())) {
					IPluginModelBase modelBase = bundleProjects.get(project
							.getName());

					BundleDescription bundleDescription = null;
					if (modelBase.isFragmentModel()) {
						BundleDescription[] hosts = modelBase
								.getBundleDescription().getHost().getHosts();
						for (BundleDescription bd : hosts) {
							if (debug)
								System.out.println("Host for "
										+ modelBase.getBundleDescription()
												.getSymbolicName() + ": "
										+ bd.getSymbolicName());
							bundleDescription = bd;
						}
					} else {
						bundleDescription = modelBase.getBundleDescription();
					}

					if (bundleDescription != null) {
						String symbolicName = bundleDescription
								.getSymbolicName();
						String bundleName = bundleDescription.getName();

						bundlesToStart.add(symbolicName);

						if (name.length() > 0)
							name.append(" ");
						if (bundleName != null)
							name.append(bundleName);
						else
							name.append(symbolicName);
					}
				}
			}
		}
	}

	@Override
	protected String getName(ILaunchConfigurationType type) {
		if (name != null)
			return name.toString();
		else
			return "SLC";
	}

}
