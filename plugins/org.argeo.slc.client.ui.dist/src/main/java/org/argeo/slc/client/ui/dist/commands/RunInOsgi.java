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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.core.execution.tasks.JvmProcess;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osgi.framework.Bundle;

/** <b>UNDER DEVELOPMENT</b>. Download and prepare an OSGi runtime */
public class RunInOsgi extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory.getLog(RunInOsgi.class);

	private Repository repository;
	private String workspace;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		InputStream jarStream = null;
		OutputStream out = null;
		Writer writer = null;
		Session session = null;
		try {
			// Target directory
			Bundle distPluginBundle = DistPlugin.getDefault().getBundle();
			File baseDirectory = distPluginBundle.getBundleContext()
					.getDataFile("runInOSGi");
			if (baseDirectory.exists())
				FileUtils.deleteDirectory(baseDirectory);
			File targetDirectory = new File(baseDirectory, "lib");
			targetDirectory.mkdirs();
			File confDir = new File(baseDirectory, "configuration");
			confDir.mkdirs();
			File dataDir = new File(baseDirectory, "data");
			dataDir.mkdirs();

			session = repository.login(workspace);
			NodeIterator bundles = listBundleArtifacts(session);

			if (log.isDebugEnabled())
				log.debug("## Copying to " + targetDirectory);

			File equinoxJar = null;
			List<File> files = new ArrayList<File>();
			bundles: while (bundles.hasNext()) {
				Node bundleNode = bundles.nextNode();
				String symbolicName = JcrUtils.get(bundleNode,
						SLC_SYMBOLIC_NAME);

				// skip sources
				if (symbolicName.endsWith(".source"))
					continue bundles;
				// skip eclipse
				if (symbolicName.startsWith("org.eclipse")
						&& !symbolicName.equals("org.eclipse.osgi"))
					continue bundles;
				if (symbolicName.equals("org.polymap.openlayers.rap.widget"))
					continue bundles;

				File targetFile = new File(targetDirectory,
						bundleNode.getName());
				out = new FileOutputStream(targetFile);
				jarStream = bundleNode.getNode(Node.JCR_CONTENT)
						.getProperty(Property.JCR_DATA).getBinary().getStream();
				IOUtils.copy(jarStream, out);
				if (symbolicName.equals("org.eclipse.osgi"))
					equinoxJar = targetFile;
				else
					files.add(targetFile);
				if (log.isDebugEnabled())
					log.debug("Copied " + targetFile.getName());

				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(jarStream);
			}

			StringBuffer osgiBundles = new StringBuffer("osgi.bundles=");
			for (int i = 0; i < files.size(); i++) {
				if (i != 0)
					osgiBundles.append(',');
				osgiBundles.append(files.get(i).getName());
			}

			File confIni = new File(confDir, "config.ini");
			writer = new FileWriter(confIni);
			writer.write(osgiBundles.toString());
			IOUtils.closeQuietly(writer);

			Map<String, String> configuration = new HashMap<String, String>();
			configuration.put("osgi.configuration.area",
					confDir.getCanonicalPath());
			configuration.put("osgi.instance.area", dataDir.getCanonicalPath());
			// Do clean
			configuration.put("osgi.clean", "true");

			JvmProcess osgiRuntime = new JvmProcess();
			osgiRuntime.setExecDir(baseDirectory.getCanonicalPath());
			if (equinoxJar == null)
				throw new SlcException("Cannot find OSGi runtime.");
			osgiRuntime.setMainJar(equinoxJar.getCanonicalPath());
			osgiRuntime.arg("-configuration", confDir.getCanonicalPath())
					.arg("-data", dataDir.getCanonicalPath())
					.arg("-console", "7777").arg("-clean");
			osgiRuntime.setLogCommand(true);
			osgiRuntime.afterPropertiesSet();
			osgiRuntime.run();

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

	static NodeIterator listBundleArtifacts(Session session)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String bundleArtifactsSelector = "bundleArtifacts";
		Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
				bundleArtifactsSelector);

		Ordering order = factory.ascending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
		Ordering[] orderings = { order };

		QueryObjectModel query = factory.createQuery(source, null, orderings,
				null);

		QueryResult result = query.execute();
		return result.getNodes();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

}
