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

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.util.TraversingItemVisitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Make sure than Maven and OSGi metadata are consistent */
public class NormalizeDistribution extends AbstractHandler implements SlcNames {
	public final static String ID = DistPlugin.ID + ".normalizeDistribution";
	public final static String PARAM_WORKSPACE = "workspace";
	public final static String DEFAULT_LABEL = "Normalize";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	private final static Log log = LogFactory
			.getLog(NormalizeDistribution.class);

	private Repository repository;
	private String artifactBasePath = "/";

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String workspace = event.getParameter(PARAM_WORKSPACE);
		NormalizeJob job;
		try {
			job = new NormalizeJob(repository.login(workspace));
		} catch (RepositoryException e) {
			throw new SlcException("Cannot normalize " + workspace, e);
		}
		job.setUser(true);
		job.schedule();
		return null;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	private class NormalizeJob extends Job {
		private Session session;

		public NormalizeJob(Session session) {
			super("Normalize Distribution");
			this.session = session;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// Session session = null;
			try {
				// session = repository.login(workspace);
				// QueryManager qm = session.getWorkspace().getQueryManager();
				// Query query = qm
				// .createQuery(
				// "select * from [nt:file] where NAME([nt:file]) like '%.jar'",
				// Query.JCR_SQL2);
				// // Query query = qm.createQuery("//*jar", Query.XPATH);
				// long count = query.execute().getRows().getSize();
				// if (log.isDebugEnabled())
				// log.debug("Count: " + count);
				// long count = query.execute().getRows().nextRow()
				// .getValue("count").getLong();
				Query countQuery = session
						.getWorkspace()
						.getQueryManager()
						.createQuery("select file from [nt:file] as file",
								Query.JCR_SQL2);
				QueryResult result = countQuery.execute();
				Long expectedCount = result.getNodes().getSize();

				monitor.beginTask("Normalize "
						+ session.getWorkspace().getName(),
						expectedCount.intValue());
				NormalizingTraverser tiv = new NormalizingTraverser(monitor);
				session.getNode(artifactBasePath).accept(tiv);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DistPlugin.ID,
						"Cannot normalize distribution "
								+ session.getWorkspace().getName(), e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
			return Status.OK_STATUS;
		}

	}

	private class NormalizingTraverser extends TraversingItemVisitor {
		IProgressMonitor monitor;

		public NormalizingTraverser(IProgressMonitor monitor) {
			super();
			this.monitor = monitor;
		}

		@Override
		protected void entering(Property property, int level)
				throws RepositoryException {
		}

		@Override
		protected void entering(Node node, int level)
				throws RepositoryException {
			if (node.isNodeType(NodeType.NT_FILE)) {
				if (jarFileIndexer.support(node.getPath()))
					if (artifactIndexer.support(node.getPath())) {
						monitor.subTask(node.getName());
						artifactIndexer.index(node);
						jarFileIndexer.index(node);
						node.getSession().save();
						monitor.worked(1);
						if (log.isDebugEnabled())
							log.debug("Processed " + node);
					}
			}
		}

		@Override
		protected void leaving(Property property, int level)
				throws RepositoryException {
		}

		@Override
		protected void leaving(Node node, int level) throws RepositoryException {
		}

	}
}
