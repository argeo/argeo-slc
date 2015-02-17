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
package org.argeo.slc.client.ui.dist.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.core.execution.tasks.JvmProcess;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.aether.artifact.Artifact;

/** <b>UNDER DEVELOPMENT</b>. Download and prepare an OSGi runtime */
public class RunInOsgi extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory.getLog(RunInOsgi.class);

	public final static String ID = DistPlugin.ID + ".runInOsgi";
	public final static String DEFAULT_LABEL = "Run in OSGi";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/runInOsgi.gif");

	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_MODULE_PATH = "modulePath";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String workspace = event.getParameter(PARAM_WORKSPACE_NAME);
		String modulePath = event.getParameter(PARAM_MODULE_PATH);
		String port = System.getProperty("argeo.server.port.http");
		// String localMavenBase = System.getProperty("user.home")
		// + "/.m2/repository";

		InputStream jarStream = null;
		OutputStream out = null;
		Writer writer = null;
		Session session = null;
		try {
			// Bundle distPluginBundle = DistPlugin.getDefault().getBundle();
			// File baseDir = distPluginBundle.getBundleContext().getDataFile(
			// "runInOSGi");
			File baseDir = new File(System.getProperty("java.io.tmpdir")
					+ "/runInOSGi-" + System.getProperty("user.name"));
			if (baseDir.exists())
				FileUtils.deleteDirectory(baseDir);
			File libDir = new File(baseDir, "lib");
			libDir.mkdirs();
			File confDir = new File(baseDir, "configuration");
			confDir.mkdirs();
			File dataDir = new File(baseDir, "data");
			dataDir.mkdirs();

			session = repository.login(workspace);

			// NodeIterator bundles = listBundleArtifacts(session);
			// if (log.isDebugEnabled())
			// log.debug("## Copying to " + libDir);
			//
			// File equinoxJar = null;
			// List<File> files = new ArrayList<File>();
			// bundles: while (bundles.hasNext()) {
			// Node bundleNode = bundles.nextNode();
			// String symbolicName = JcrUtils.get(bundleNode,
			// SLC_SYMBOLIC_NAME);
			//
			// // skip sources
			// if (symbolicName.endsWith(".source"))
			// continue bundles;
			// // skip eclipse
			// if (symbolicName.startsWith("org.eclipse")
			// && !symbolicName.equals("org.eclipse.osgi"))
			// continue bundles;
			// if (symbolicName.equals("org.polymap.openlayers.rap.widget"))
			// continue bundles;
			//
			// File targetFile = new File(libDir, bundleNode.getName());
			// out = new FileOutputStream(targetFile);
			// jarStream = bundleNode.getNode(Node.JCR_CONTENT)
			// .getProperty(Property.JCR_DATA).getBinary().getStream();
			// IOUtils.copy(jarStream, out);
			// if (symbolicName.equals("org.eclipse.osgi"))
			// equinoxJar = targetFile;
			// else
			// files.add(targetFile);
			// if (log.isDebugEnabled())
			// log.debug("Copied " + targetFile.getName());
			//
			// IOUtils.closeQuietly(out);
			// IOUtils.closeQuietly(jarStream);
			// }
			//
			// StringBuffer osgiBundles = new StringBuffer("osgi.bundles=");
			// for (int i = 0; i < files.size(); i++) {
			// if (i != 0)
			// osgiBundles.append(',');
			// osgiBundles.append(files.get(i).getName());
			// }

			String equinoxJar = null;

			Node distModule = session.getNode(modulePath);
			NodeIterator coordinates = distModule.getNode(SLC_MODULES)
					.getNodes();
			StringBuilder conf = new StringBuilder(1024 * 1024);
			conf.append("osgi.clean=true\n");
			conf.append("osgi.console=7777\n");
			// conf.append("osgi.console.enable.builtin=true\n");

			conf.append("osgi.bundles=");
			coords: while (coordinates.hasNext()) {
				Node coord = coordinates.nextNode();
				// String category =
				// coord.getProperty(SLC_CATEGORY).getString();
				String name = coord.getProperty(SLC_NAME).getString();
				String version = coord.getProperty(SLC_VERSION).getString();
				Artifact artifact = RepoUtils.asArtifact(coord);
				String path = MavenConventionsUtils.artifactPath("", artifact);
				String url = "http://localhost:" + port + "/data/public/java/"
						+ workspace + path;
				if (log.isDebugEnabled())
					log.debug(url);
				File f = new File(libDir, name + "-" + version + ".jar");
				FileUtils.copyURLToFile(new URL(url), f);
				if (name.equals("org.eclipse.osgi")) {
					// File f = new File(localMavenBase + path);
					// if (!f.exists())
					// FileUtils.copyURLToFile(new URL(url), f);
					equinoxJar = f.getCanonicalPath();
					continue coords;
				}
				conf.append(f.getName());
				if (coordinates.hasNext())
					conf.append(",\\\n");
			}

			File confIni = new File(confDir, "config.ini");
			writer = new FileWriter(confIni);
			writer.write(conf.toString());
			IOUtils.closeQuietly(writer);

			// Map<String, String> configuration = new HashMap<String,
			// String>();
			// configuration.put("osgi.configuration.area",
			// confDir.getCanonicalPath());
			// configuration.put("osgi.instance.area",
			// dataDir.getCanonicalPath());
			// // Do clean
			// configuration.put("osgi.clean", "true");

			JvmProcess osgiRuntime = new JvmProcess();
			osgiRuntime.setExecDir(baseDir.getCanonicalPath());
			if (equinoxJar == null)
				throw new SlcException("Cannot find OSGi runtime.");
			osgiRuntime.setMainJar(equinoxJar);
			osgiRuntime.arg("-configuration", confDir.getCanonicalPath()).arg(
					"-data", dataDir.getCanonicalPath());
			// .arg("-console", "7777").arg("-clean");
			osgiRuntime.setLogCommand(true);
			osgiRuntime.afterPropertiesSet();
			Job job = new RunInOsgiJob(osgiRuntime);
			job.schedule();
			// osgiRuntime.run();

			// Map<String, String> configuration = new HashMap<String,
			// String>();
			// configuration.put("osgi.configuration.area",
			// confDir.getCanonicalPath());
			// configuration.put("osgi.instance.area",
			// dataDir.getCanonicalPath());
			// // Do clean
			// configuration.put("osgi.clean", "true");
			// ServiceLoader<FrameworkFactory> ff = ServiceLoader
			// .load(FrameworkFactory.class);
			// FrameworkFactory frameworkFactory = ff.iterator().next();
			// Framework framework =
			// frameworkFactory.newFramework(configuration);
			// framework.start();
			// BundleContext testBundleContext = framework.getBundleContext();

			// for (int i = 0; i < files.size(); i++) {
			// testBundleContext.installBundle("file://"
			// + files.get(i).getCanonicalPath());
			// }
			//
			// Bundle[] testBundles = testBundleContext.getBundles();
			// for (Bundle bundle : testBundles) {
			// if (log.isDebugEnabled())
			// log.debug(bundle.getSymbolicName() + " "
			// + bundle.getVersion());
			// }

		} catch (Exception e) {
			ErrorFeedback.show("Cannot run in OSGi", e);
		} finally {
			IOUtils.closeQuietly(jarStream);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(writer);
			JcrUtils.logoutQuietly(session);
		}

		return null;
	}

	// private NodeIterator listBundleArtifacts(Session session)
	// throws RepositoryException {

	// QueryManager queryManager = session.getWorkspace().getQueryManager();
	// QueryObjectModelFactory factory = queryManager.getQOMFactory();
	//
	// final String bundleArtifactsSelector = "bundleArtifacts";
	// Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
	// bundleArtifactsSelector);
	//
	// Ordering order = factory.ascending(factory.propertyValue(
	// bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
	// Ordering[] orderings = { order };
	//
	// QueryObjectModel query = factory.createQuery(source, null, orderings,
	// null);
	//
	// QueryResult result = query.execute();
	// return result.getNodes();
	// }

	private class RunInOsgiJob extends Job {
		final JvmProcess osgiRuntime;

		public RunInOsgiJob(JvmProcess osgiRuntime) {
			super("OSGi Test");
			this.osgiRuntime = osgiRuntime;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			osgiRuntime.setSynchronous(false);
			osgiRuntime.run();
			while (!monitor.isCanceled()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// silent
				}

				if (monitor.isCanceled()) {
					osgiRuntime.kill();
					return Status.CANCEL_STATUS;
				}
				if (!osgiRuntime.isRunning())
					break;
			}
			return Status.OK_STATUS;
		}

	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
