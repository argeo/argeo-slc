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
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.MarkAsRelevantCategory;
import org.argeo.slc.client.ui.dist.utils.NodeViewerComparator;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Show all category base (currently only Aether group base) contained in a
 * given workspace as filter-able table. Enable to definition of which of them
 * should be managed as modular distribution
 */
public class WkspCategoryBaseListPage extends FormPage implements SlcNames {

	final static String PAGE_ID = "WkspCategoryBaseListPage";

	// Business Objects
	private Session session;

	// This page widgets
	private NodeViewerComparator comparator;
	private TableViewer viewer;
	private FormToolkit tk;
	private Composite header;
	private Text filterTxt;
	private final static String FILTER_HELP_MSG = "Enter filter criterion separated by a space";

	public WkspCategoryBaseListPage(FormEditor formEditor, String title,
			Session session) {
		super(formEditor, PAGE_ID, title);
		this.session = session;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();

		form.setText("Category base definition");
		form.setMessage("Define relevant group base objects",
				IMessageProvider.NONE);

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
			// form.addListener(SWT.RESIZE, new Listener() does not work
			public void controlResized(ControlEvent e) {
				refreshLayout();
			}

			public void controlMoved(ControlEvent e) {
			}
		});
		refresh();
	}

	private void refresh() {
		final List<Node> result = JcrUtils.nodeIteratorToList(listGroupBase());
		viewer.setInput(result);
	}

	/** Build repository request */
	private NodeIterator listGroupBase() {
		try {
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();

			Selector source = factory.selector(SlcTypes.SLC_GROUP_BASE,
					SlcTypes.SLC_MODULE_COORDINATES);

			// Create a dynamic operand for each property on which we want to
			// filter
			DynamicOperand catDO = factory.propertyValue(
					source.getSelectorName(), SlcNames.SLC_CATEGORY);
			DynamicOperand nameDO = factory.propertyValue(
					source.getSelectorName(), SlcNames.SLC_NAME);

			String filter = filterTxt.getText();

			Constraint defaultC = null;
			// Build constraints based the textArea content
			if (filter != null && !"".equals(filter.trim())) {
				// Parse the String
				String[] strs = filter.trim().split(" ");
				for (String token : strs) {
					token = token.replace('*', '%');
					StaticOperand so = factory.literal(session
							.getValueFactory().createValue("%" + token + "%"));

					Constraint currC = factory.comparison(catDO,
							QueryObjectModelFactory.JCR_OPERATOR_LIKE, so);
					currC = factory.or(currC, factory.comparison(nameDO,
							QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));

					if (defaultC == null)
						defaultC = currC;
					else
						defaultC = factory.and(defaultC, currC);
				}
			}

			QueryObjectModel query = factory.createQuery(source, defaultC,
					null, null);
			QueryResult result = query.execute();
			return result.getNodes();
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unable to refresh group list for workspace "
							+ getEditorInput().getName(), re);
		}
	}

	private void createFilterPart(Composite parent) {
		header = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;
		header.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		header.setLayoutData(gd);

		// Text Area to filter
		filterTxt = tk.createText(header, "", SWT.BORDER | SWT.SINGLE
				| SWT.SEARCH | SWT.CANCEL);
		filterTxt.setMessage(FILTER_HELP_MSG);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.grabExcessHorizontalSpace = true;
		filterTxt.setLayoutData(gd);
		filterTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				refresh();
			}
		});

		Button resetBtn = tk.createButton(header, null, SWT.PUSH);
		resetBtn.setImage(DistImages.IMG_REPO_READONLY);
		resetBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				resetFilter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void resetFilter() {
		filterTxt.setText("");
		filterTxt.setMessage(FILTER_HELP_MSG);
	}

	private void createTableViewer(Composite parent) {
		// helpers to enable sorting by column
		List<String> propertiesList = new ArrayList<String>();
		List<Integer> propertyTypesList = new ArrayList<Integer>();

		// Define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn col;
		// Name
		// TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		// col.getColumn().setWidth(220);
		// col.getColumn().setText("Category");
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// return JcrUtils.get((Node) element, SlcNames.SLC_CATEGORY);
		// }
		// });
		// col.getColumn().addSelectionListener(getSelectionAdapter(0));
		// propertiesList.add(SlcNames.SLC_CATEGORY);
		// propertyTypesList.add(PropertyType.STRING);

		// Group base name
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(220);
		col.getColumn().setText("Group Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_GROUP_BASE_ID);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(0));
		propertiesList.add(SLC_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(80);
		col.getColumn().setText("Is category");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return ((Node) element)
							.isNodeType(SlcTypes.SLC_RELEVANT_CATEGORY) ? "Yes"
							: "-";
				} catch (RepositoryException e) {
					throw new SlcException("unable to check type of node "
							+ element, e);
				}
			}
		});
		// col.getColumn().addSelectionListener(getSelectionAdapter(2));
		// propertiesList.add(SLC_VERSION);
		// propertyTypesList.add(PropertyType.STRING);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new DistributionsContentProvider());
		getSite().setSelectionProvider(viewer);

		comparator = new NodeViewerComparator(0,
				NodeViewerComparator.ASCENDING, propertiesList,
				propertyTypesList);
		viewer.setComparator(comparator);

		// Context Menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		// Double click
		viewer.addDoubleClickListener(new DoubleClickListener());
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	/** Programmatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		// Build conditions
		// Mark as category base
		Object firstElement = ((IStructuredSelection) viewer.getSelection())
				.getFirstElement();
		boolean showMark = false;
		try {
			showMark = !((Node) firstElement)
					.isNodeType(SlcTypes.SLC_RELEVANT_CATEGORY);
		} catch (RepositoryException e) {
			throw new SlcException("unable to check type of node "
					+ firstElement, e);
		}
		CommandUtils.refreshCommand(menuManager, window,
				MarkAsRelevantCategory.ID,
				MarkAsRelevantCategory.DEFAULT_LABEL,
				MarkAsRelevantCategory.DEFAULT_ICON, showMark);
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

	/* LOCAL CLASSES */
	private class DistributionsContentProvider implements
			IStructuredContentProvider {
		// we keep a cache of the Nodes in the content provider to be able to
		// manage long request
		private List<Node> nodes;

		public void dispose() {
		}

		// We expect a list of nodes as a new input
		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			nodes = (List<Node>) newInput;
		}

		public Object[] getElements(Object arg0) {
			return nodes.toArray();
		}
	}

	private class DoubleClickListener implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			Object obj = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT)) {
						GenericBundleEditorInput gaei = new GenericBundleEditorInput(
								node);
						DistPlugin.getDefault().getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.openEditor(gaei, GenericBundleEditor.ID);
					}
				}
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Repository error while getting node info", re);
			} catch (PartInitException pie) {
				throw new ArgeoException(
						"Unexepected exception while opening artifact editor",
						pie);
			}
		}
	}

	/**
	 * UI Trick to put scroll bar on the table rather than on the scrollform
	 */
	private void refreshLayout() {
		// Compute desired table size
		int maxH = getManagedForm().getForm().getSize().y;
		int maxW = getManagedForm().getForm().getParent().getSize().x;
		maxH = maxH - header.getSize().y;
		final Table table = viewer.getTable();
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true);

		// when table height is less than 200 px, we let the scroll bar on the
		// scrollForm
		// FIXME substract some spare space. There is room here for optimization
		gd.heightHint = Math.max(maxH - 35, 200);
		gd.widthHint = Math.max(maxW - 35, 200);

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
}