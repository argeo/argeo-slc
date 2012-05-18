/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
import java.util.List;

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
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class RunInOsgi extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory.getLog(RunInOsgi.class);

	private Repository repository;
	private String workspace;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		File targetDirectory = new File(
				"/home/mbaudier/dev/work/120517-ArgeoTP/" + workspace);

		InputStream jarStream = null;
		OutputStream out = null;
		Writer writer = null;
		Session session = null;
		try {
			FileUtils.deleteDirectory(targetDirectory);
			targetDirectory.mkdirs();

			session = repository.login(workspace);
			NodeIterator bundles = listBundleArtifacts(session);

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
				if (!symbolicName.equals("org.eclipse.osgi"))
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

			File confDir = new File(targetDirectory, "configuration");
			confDir.mkdirs();
			File confIni = new File(confDir, "config.ini");
			writer = new FileWriter(confIni);
			writer.write(osgiBundles.toString());
			IOUtils.closeQuietly(writer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
