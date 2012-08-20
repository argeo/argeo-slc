package org.argeo.slc.client.ui.dist.editors;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyType;
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
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.DeleteArtifacts;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.utils.NodeViewerComparator;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.osgi.framework.Constants;

/** Table giving an overview of an OSGi distribution */
public class DistributionOverviewPage extends FormPage implements SlcNames {
	final static String PAGE_ID = "distributionOverviewPage";

	private TableViewer viewer;
	private Session session;

	private NodeViewerComparator comparator;

	public DistributionOverviewPage(FormEditor formEditor, String title,
			Session session) {
		super(formEditor, PAGE_ID, title);
		this.session = session;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		// helpers to enable sorting by column
		List<String> propertiesList = new ArrayList<String>();
		List<Integer> propertyTypesList = new ArrayList<Integer>();

		// Define the TableViewer
		viewer = new TableViewer(form.getBody(), SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn col = new TableViewerColumn(viewer, SWT.V_SCROLL);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Symbolic name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(0));
		propertiesList.add(SLC_SYMBOLIC_NAME);
		propertyTypesList.add(PropertyType.STRING);

		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(1));
		propertiesList.add(SLC_BUNDLE_VERSION);
		propertyTypesList.add(PropertyType.STRING);

		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText("Group ID");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_GROUP_ID);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(2));
		propertiesList.add(SLC_GROUP_ID);
		propertyTypesList.add(PropertyType.STRING);

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
		col.getColumn().addSelectionListener(getSelectionAdapter(3));
		propertiesList.add(SLC_ + Constants.BUNDLE_NAME);
		propertyTypesList.add(PropertyType.STRING);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new DistributionsContentProvider());
		getSite().setSelectionProvider(viewer);

		viewer.setInput(session);
		comparator = new NodeViewerComparator(1,
				NodeViewerComparator.DESCENDING, propertiesList,
				propertyTypesList);
		viewer.setComparator(comparator);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	/** force refresh of the artifact list */
	public void refresh() {
		viewer.refresh();
	}

	/** Programatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();

		// Build conditions depending on element type (repo or workspace)

		// Delete selected artifacts
		CommandHelpers.refreshCommand(menuManager, window, DeleteArtifacts.ID,
				DeleteArtifacts.DEFAULT_LABEL,
				DeleteArtifacts.DEFAULT_ICON_PATH, true);

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

	private SelectionAdapter getSelectionAdapter(final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				comparator.setColumn(index);
				int dir = table.getSortDirection();
				if (table.getSortColumn() == table.getColumn(index)) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				table.setSortDirection(dir);
				table.setSortColumn(table.getColumn(index));
				viewer.refresh();
			}
		};
		return selectionAdapter;
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
	//
	// private class BoundedLayout extends Layout {
	// protected Layout delegateLayout;
	//
	// protected Method computeSizeMethod;
	// protected Method layoutMethod;
	//
	// protected boolean widthBound;
	//
	// public BoundedLayout(Layout delegateLayout, boolean widthBound) {
	// setDelegateLayout(delegateLayout);
	// this.widthBound = widthBound;
	// }
	//
	// public Layout getDelegateLayout() {
	// return delegateLayout;
	// }
	//
	// public void setDelegateLayout(Layout delegateLayout) {
	// this.delegateLayout = delegateLayout;
	//
	// try {
	// computeSizeMethod = delegateLayout.getClass()
	// .getDeclaredMethod("computeSize", Composite.class,
	// int.class, int.class, boolean.class);
	// computeSizeMethod.setAccessible(true);
	//
	// layoutMethod = delegateLayout.getClass().getDeclaredMethod(
	// "layout", Composite.class, boolean.class);
	// layoutMethod.setAccessible(true);
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// protected Point computeSize(Composite composite, int wHint, int hHint,
	// boolean flushCache) {
	// // get comp size to make sure we don't let any children exceed it
	// Point compSize = composite.getSize();
	//
	// try {
	// Point layoutComputedSize = (Point) computeSizeMethod.invoke(
	// delegateLayout, composite, wHint, hHint, flushCache);
	//
	// if (widthBound) {
	// layoutComputedSize.x = Math.min(compSize.x,
	// layoutComputedSize.x);
	// } else {
	// layoutComputedSize.y = Math.min(compSize.y,
	// layoutComputedSize.y);
	// }
	//
	// return layoutComputedSize;
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// @Override
	// protected void layout(Composite composite, boolean flushCache) {
	// try {
	// layoutMethod.invoke(delegateLayout, composite, flushCache);
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }
}