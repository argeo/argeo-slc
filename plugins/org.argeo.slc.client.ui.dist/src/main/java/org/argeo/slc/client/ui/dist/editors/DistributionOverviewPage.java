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
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
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
import org.eclipse.swt.widgets.Label;
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

/** Table giving an overview of an OSGi distribution with corresponding filters */
public class DistributionOverviewPage extends FormPage implements SlcNames {
	final static String PAGE_ID = "distributionOverviewPage";
	// final private static Log log = LogFactory
	// .getLog(DistributionOverviewPage.class);

	// Business Objects
	private Session session;

	// This page widgets
	private NodeViewerComparator comparator;
	private TableViewer viewer;
	private Text artifactTxt;
	private FormToolkit tk;
	private Composite header;

	// private Section headerSection;

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

		viewer.setInput(session);
		resetFilter();

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

	/** Build repository request */
	private NodeIterator listBundleArtifacts(Session session)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String bundleArtifactsSelector = "bundleArtifacts";
		Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
				bundleArtifactsSelector);

		// Create a dynamic operand for each property on which we want to filter
		DynamicOperand symbNameDO = factory.propertyValue(
				source.getSelectorName(), SlcNames.SLC_SYMBOLIC_NAME);
		DynamicOperand versionDO = factory.propertyValue(
				source.getSelectorName(), SlcNames.SLC_BUNDLE_VERSION);
		DynamicOperand nameDO = factory.propertyValue(source.getSelectorName(),
				DistConstants.SLC_BUNDLE_NAME);

		// Default Constraint: no source artifacts
		Constraint defaultC = factory.not(factory.comparison(
				symbNameDO,
				QueryObjectModelFactory.JCR_OPERATOR_LIKE,
				factory.literal(session.getValueFactory().createValue(
						"%.source"))));

		// Build constraints based the textArea content
		String artifactTxtVal = artifactTxt.getText();
		if (!"".equals(artifactTxtVal.trim())) {
			// Parse the String
			String[] strs = artifactTxtVal.trim().split(" ");
			for (String token : strs) {
				token = token.replace('*', '%');
				StaticOperand so = factory.literal(session.getValueFactory()
						.createValue("%" + token + "%"));

				Constraint currC = factory.comparison(symbNameDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so);
				currC = factory.or(currC, factory.comparison(versionDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));
				currC = factory.or(currC, factory.comparison(nameDO,
						QueryObjectModelFactory.JCR_OPERATOR_LIKE, so));

				defaultC = factory.and(defaultC, currC);
			}
		}

		Ordering order = factory.descending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_BUNDLE_VERSION));
		Ordering order2 = factory.ascending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
		Ordering[] orderings = { order, order2 };

		QueryObjectModel query = factory.createQuery(source, defaultC,
				orderings, null);

		QueryResult result = query.execute();
		return result.getNodes();

	}

	private void createFilterPart(Composite parent) {
		header = tk.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		header.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		header.setLayoutData(gd);

		// TODO display real repository information
		// Title: some meta information
		Label lbl = tk.createLabel(header, "Current repository: ", SWT.NONE);

		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		lbl.setLayoutData(gd);

		// Text Area to filter
		artifactTxt = tk.createText(header, "", SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.grabExcessHorizontalSpace = true;
		artifactTxt.setLayoutData(gd);
		artifactTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				refreshFilteredList();
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
		artifactTxt.setText("");
		artifactTxt.setMessage("Enter filter criterion separated by a space");
		viewer.refresh();
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

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element,
						DistConstants.SLC_BUNDLE_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(0));
		propertiesList.add(DistConstants.SLC_BUNDLE_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Symbolic name
		col = new TableViewerColumn(viewer, SWT.V_SCROLL);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Symbolic name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(1));
		propertiesList.add(SLC_SYMBOLIC_NAME);
		propertyTypesList.add(PropertyType.STRING);

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});
		col.getColumn().addSelectionListener(getSelectionAdapter(2));
		propertiesList.add(SLC_BUNDLE_VERSION);
		propertyTypesList.add(PropertyType.STRING);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new DistributionsContentProvider());
		getSite().setSelectionProvider(viewer);

		comparator = new NodeViewerComparator(2,
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

	/** force refresh of the artifact list */
	public void refresh() {
		viewer.refresh();
	}

	/** Programmatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();

		// Build conditions

		// Delete selected artifacts
		CommandHelpers.refreshCommand(menuManager, window, DeleteArtifacts.ID,
				DeleteArtifacts.DEFAULT_LABEL,
				DeleteArtifacts.DEFAULT_ICON_PATH, true);

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
		// private Session session;

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// session = (Session) newInput;
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
		// FIXME substract some spare space. Here is room for optimization
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