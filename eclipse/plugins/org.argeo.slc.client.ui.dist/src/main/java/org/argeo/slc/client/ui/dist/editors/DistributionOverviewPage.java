package org.argeo.slc.client.ui.dist.editors;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.osgi.framework.Constants;

/** Table giving an overview of an OSGi distribution */
class DistributionOverviewPage extends FormPage implements SlcNames {
	private TableViewer viewer;
	private Session session;

	public DistributionOverviewPage(FormEditor formEditor, String title,
			Session session) {
		super(formEditor, "distributionPage", title);
		this.session = session;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		// Define the TableViewer
		viewer = new TableViewer(form.getBody(), SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Symbolic name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText("Group ID");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_GROUP_ID);
			}
		});
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_
						+ Constants.BUNDLE_NAME);
			}
		});

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new DistributionsContentProvider());
		viewer.setInput(session);
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
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

}