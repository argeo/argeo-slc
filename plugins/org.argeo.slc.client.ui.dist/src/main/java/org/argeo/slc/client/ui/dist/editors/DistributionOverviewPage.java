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
package org.argeo.slc.client.ui.dist.editors;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.Constants;

/** Table giving an overview of an OSGi distribution with corresponding filters */
public class DistributionOverviewPage extends FormPage implements SlcNames {
	final static String PAGE_ID = "distributionOverviewPage";
	final private static Log log = LogFactory
			.getLog(DistributionOverviewPage.class);

	// Business Objects
	private Session session;

	// This page widgets
	private NodeViewerComparator comparator;
	private TableViewer viewer;
	private Text artifactTxt;
	private FormToolkit tk;
	private Composite header;
	private Section headerSection;

	public DistributionOverviewPage(FormEditor formEditor, String title,
			Session session) {
		super(formEditor, PAGE_ID, title);
		this.session = session;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();

		// Main Layout
		GridLayout layout = new GridLayout(1, false);
		Composite body = form.getBody();
		body.setLayout(layout);

		// Add the filter section
		createFilterPart(body);
		// Add the table
		createTableViewer(body);

		// Add a listener to enable custom resize process
		form.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				refreshLayout();
			}

			public void controlMoved(ControlEvent e) {
			}
		});

		// This below doesn not work; the listener must be added as a control
		// listener to be correctly notified when resize events happen

		// form.addListener(SWT.RESIZE, new Listener() {
		// public void handleEvent(Event event) {
		// log.debug("Form resized ....");
		// }
		// });

	}

	private void createFilterPart(Composite parent) {
		header = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		header.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		header.setLayoutData(gd);

		// Artifact Name
		tk.createLabel(header, "Artifact name: ", SWT.NONE);
		artifactTxt = tk.createText(header, "", SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.grabExcessHorizontalSpace = true;
		artifactTxt.setLayoutData(gd);
		artifactTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				refreshFilteredList();
			}
		});

		headerSection = tk.createSection(header, Section.TWISTIE);
		headerSection.setText("Advanced filters");
		headerSection.setExpanded(false);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		headerSection.setLayoutData(gd);

		Composite body = new Composite(headerSection, SWT.FILL);
		headerSection.setClient(body);

		// Layout
		layout = new GridLayout(2, false);
		body.setLayout(layout);

		// Artifact Name
		tk.createLabel(body, "Add some more filters here ", SWT.NONE);
		// lbl = tk.createLabel(body, "Artifact name: ", SWT.NONE);
		// artifactTxt = tk.createText(body, "", SWT.BORDER | SWT.SINGLE);
		// gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		// gd.grabExcessHorizontalSpace = true;
		// artifactTxt.setLayoutData(gd);
		// artifactTxt.addModifyListener(new ModifyListener() {
		//
		// public void modifyText(ModifyEvent event) {
		// refreshFilteredList();
		// }
		// });
	}

	private void refreshFilteredList() {
		viewer.refresh();
	}

	private void createTableViewer(Composite parent) {

		// helpers to enable sorting by column
		List<String> propertiesList = new ArrayList<String>();
		List<Integer> propertyTypesList = new ArrayList<Integer>();

		// Define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
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
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true);
		gd.heightHint = 300;
		table.setLayoutData(gd);

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

	/**
	 * UI Trick to put scroll bar on the table rather than on the scrollform
	 */
	private void refreshLayout() {
		// Compute desired table size
		int maxH = getManagedForm().getForm().getSize().y;
		int maxW = getManagedForm().getForm().getParent().getSize().x;
		// int maxW = getManagedForm().getForm().getSize().x;
		maxH = maxH - header.getSize().y;

		// maxH = maxH - headerSection.getSize().y
		// - headerSection.getClient().getSize().y;

		// Set
		final Table table = viewer.getTable();
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true);
		// when table height is less than 200 px, we let the scroll bar on the
		// scrollForm

		// FIXME substract some spare space. Here is room for optimization
		gd.heightHint = Math.max(maxH - 70, 200);
		gd.widthHint = Math.max(maxW - 40, 200);

		table.setLayoutData(gd);
		getManagedForm().reflow(true);
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			refreshLayout();
		}
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

	NodeIterator listBundleArtifacts(Session session)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String bundleArtifactsSelector = "bundleArtifacts";
		Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
				bundleArtifactsSelector);

		String artifactTxtVal = artifactTxt.getText();

		DynamicOperand propName = factory.propertyValue(
				source.getSelectorName(), SlcNames.SLC_SYMBOLIC_NAME);
		StaticOperand propValue = factory.bindVariable("'%" + artifactTxtVal
				+ "%'");
		Constraint constraint = factory.comparison(propName,
				QueryObjectModelFactory.JCR_OPERATOR_LIKE, propValue);

		Ordering order = factory.ascending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
		Ordering[] orderings = { order };

		QueryObjectModel query = factory.createQuery(source, constraint,
				orderings, null);

		QueryResult result = query.execute();
		return result.getNodes();
	}

	private NodeIterator getArtifactsWithWhereClause(String whereClause) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Select * FROM [" + SlcTypes.SLC_BUNDLE_ARTIFACT
				+ "] AS bundleArtifacts");
		if (whereClause != null && !"".equals(whereClause.trim())) {
			strBuf.append(" WHERE ");
			strBuf.append(whereClause);
		}
		strBuf.append(" ORDER BY ");
		strBuf.append("bundleArtifacts.[" + SlcNames.SLC_SYMBOLIC_NAME + "] ");
		strBuf.append("ASC");
		try {
			if (log.isTraceEnabled())
				log.trace("Get artifacts query : " + strBuf.toString());
			Query query = session.getWorkspace().getQueryManager()
					.createQuery(strBuf.toString(), Query.JCR_SQL2);
			return query.execute().getNodes();
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unexpected error while retrieving list of artifacts", e);
		}
	}

	private String buildWhereClause() {
		StringBuffer whereClause = new StringBuffer();

		String artifactTxtVal = artifactTxt.getText();
		if (!"".equals(artifactTxtVal)) {
			whereClause.append("bundleArtifacts.[" + SlcNames.SLC_SYMBOLIC_NAME
					+ "] like ");
			whereClause.append("'%" + artifactTxtVal + "%'");
		}
		return whereClause.toString();
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

	private class DistributionsContentProvider implements
			IStructuredContentProvider {
		// private Session session;

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// session = (Session) newInput;
		}

		public Object[] getElements(Object arg0) {
			// try {
			// List<Node> nodes = JcrUtils
			// .nodeIteratorToList(listBundleArtifacts(session));
			// return nodes.toArray();
			// } catch (RepositoryException e) {
			// ErrorFeedback.show("Cannot list bundles", e);
			// return null;
			// }

			List<Node> nodes = JcrUtils
					.nodeIteratorToList(getArtifactsWithWhereClause(buildWhereClause()));
			return nodes.toArray();
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