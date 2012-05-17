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
package org.argeo.slc.client.ui.dist.views;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/**
 * Basic View to browse a maven based repository.
 * 
 * By Default size of the various bundles is not computed but it can be
 * activated the view command.
 */

public class DistributionsView extends ViewPart implements SlcNames {
	private final static Log log = LogFactory.getLog(DistributionsView.class);
	public final static String ID = DistPlugin.ID + ".distributionsView";

	private Repository repository;
	private String workspace;

	private Session session;
	private TableViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		try {
			session = repository.login(workspace);
		} catch (RepositoryException e) {
			ErrorFeedback.show("Cannot log to workspace " + workspace, e);
		}

		// Define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(200);
		col.getColumn().setText("Workspace");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new DistributionsContentProvider());
		viewer.setInput(session);
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	private static class DistributionsContentProvider implements
			IStructuredContentProvider {
		private Session session;

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			session = (Session) newInput;
		}

		public Object[] getElements(Object arg0) {
			try {
				List<Node> nodes = JcrUtils
						.nodeIteratorToList(listBundleArtifacts(session));
				return nodes.toArray();
			} catch (RepositoryException e) {
				ErrorFeedback.show("Cannot list bundles", e);
				return null;
			}
		}

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

}
