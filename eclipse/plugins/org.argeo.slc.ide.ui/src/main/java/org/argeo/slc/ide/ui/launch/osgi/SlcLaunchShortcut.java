package org.argeo.slc.ide.ui.launch.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

public class SlcLaunchShortcut extends AbstractOsgiLaunchShortcut {
	private Boolean debug = false;

	private String springOsgiExtenderId = "org.springframework.osgi.extender";
	private String slcSupportEquinoxId = "org.argeo.slc.support.equinox";
	// private String slcAgentId = "org.argeo.slc.agent";
	// private String osgiBootId = "org.argeo.slc.osgiboot";

	private ISelection selection = null;

	private final List<String> defaultBundlesToStart = new ArrayList<String>();
	public SlcLaunchShortcut() {
		super();
		defaultBundlesToStart.add(springOsgiExtenderId);
		defaultBundlesToStart.add(slcSupportEquinoxId);
		// defaultBundlesToStart.add(slcAgentId);
	}

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
}
