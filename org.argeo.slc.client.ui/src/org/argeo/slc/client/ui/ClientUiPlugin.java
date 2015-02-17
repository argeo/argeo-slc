/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.ModulesManager;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The activator class controls the plug-in life cycle */
public class ClientUiPlugin extends AbstractUIPlugin implements SlcNames {
	public static final String ID = "org.argeo.slc.client.ui";
	private static ClientUiPlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ClientUiPlugin getDefault() {
		return plugin;
	}

	/** Creates the image */
	public static Image img(String path) {
		return getImageDescriptor(path).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}

	/** Start execution module if it was stopped and vice-versa */
	public static void startStopExecutionModule(
			final ModulesManager modulesManager, Node node) {
		try {
			if (!node.isNodeType(SlcTypes.SLC_EXECUTION_MODULE))
				throw new SlcException(node + " is not an execution module");

			String name = node.getProperty(SLC_NAME).getString();
			String version = node.getProperty(SLC_VERSION).getString();
			final NameVersion nameVersion = new BasicNameVersion(name, version);
			Boolean started = node.getProperty(SLC_STARTED).getBoolean();

			Job job;
			if (started) {
				job = new Job("Stop " + nameVersion) {
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("Stop " + nameVersion, 1);
						modulesManager.stop(nameVersion);
						monitor.worked(1);
						return Status.OK_STATUS;
					}

					protected void canceling() {
						getThread().interrupt();
						super.canceling();
					}
				};
			} else {
				job = new Job("Start " + nameVersion) {
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("Start " + nameVersion, 1);
						modulesManager.start(nameVersion);
						monitor.worked(1);
						return Status.OK_STATUS;
					}

					protected void canceling() {
						getThread().interrupt();
						super.canceling();
					}
				};
			}
			job.setUser(true);
			job.schedule();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot start " + node, e);
		}

	}
}
