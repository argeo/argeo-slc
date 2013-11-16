package org.argeo.slc.akb.ui.views;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.composites.AkbItemsTableComposite;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Basic view that display a list of items with a quick search field. */
public class AkbDefaultView extends ViewPart {
	// private final static Log log = LogFactory.getLog(QuickSearchView.class);

	public static final String ID = AkbUiPlugin.PLUGIN_ID + ".akbDefaultView";

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private AkbItemsTableComposite userTableCmp;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.horizontalSpacing = gl.verticalSpacing = gl.marginWidth = 0;
		parent.setLayout(gl);

		// Create the composite that displays the list and a filter
		AkbItemsTableComposite userTableCmp = new AkbItemsTableComposite(
				parent, SWT.NO_FOCUS, session);
		userTableCmp.populate(true, false);
		userTableCmp
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Configure
		userTableCmp.getTableViewer().addDoubleClickListener(
				new ViewDoubleClickListener());
		getViewSite().setSelectionProvider(userTableCmp.getTableViewer());

		// // Filter
		// Composite cmp = new Composite(parent, SWT.NO_FOCUS);
		// cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		// createFilterPart(cmp);
		//
		// // Table
		// cmp = new Composite(parent, SWT.NO_FOCUS);
		// cmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// itemViewer = createListPart(cmp);
		//
		// // refreshFilteredList();
	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					Node currEnv = AkbJcrUtils.getCurrentTemplate(node);

					// Add Connector Alias
					Map<String, String> params = new HashMap<String, String>();
					params.put(OpenAkbNodeEditor.PARAM_NODE_JCR_ID,
							node.getIdentifier());
					params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
							currEnv.getIdentifier());

					CommandUtils.callCommand(OpenAkbNodeEditor.ID, params);
				}
			} catch (RepositoryException e) {
				throw new AkbException("Cannot open " + obj, e);
			}
		}
	}

	// private void createFilterPart(Composite parent) {
	// parent.setLayout(new GridLayout());
	// // Text Area for the filter
	// filterTxt = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH
	// | SWT.ICON_CANCEL);
	// filterTxt.setMessage(FILTER_HELP_MSG);
	// filterTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	// filterTxt.addModifyListener(new ModifyListener() {
	//
	// public void modifyText(ModifyEvent event) {
	// refreshFilteredList();
	// }
	// });
	// }
	//
	// protected TableViewer createListPart(Composite parent) {
	// TableViewer v = new TableViewer(parent);
	//
	// TableColumn singleColumn = new TableColumn(v.getTable(), SWT.V_SCROLL);
	// TableColumnLayout tableColumnLayout = new TableColumnLayout();
	// tableColumnLayout.setColumnData(singleColumn, new ColumnWeightData(85));
	// parent.setLayout(tableColumnLayout);
	//
	// // Corresponding table & style
	// Table table = v.getTable();
	// table.setLinesVisible(true);
	// table.setHeaderVisible(false);
	//
	// v.setContentProvider(new BasicNodeListContentProvider());
	// v.addDoubleClickListener(peopleUiService
	// .getNewNodeListDoubleClickListener(peopleService, null));
	// return v;
	// }

	@Override
	public void dispose() {
		userTableCmp.dispose();
		JcrUtils.logoutQuietly(session);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	// protected void refreshFilteredList() {
	// try {
	// List<Node> persons = JcrUtils.nodeIteratorToList(doSearch(session,
	// filterTxt.getText(), PeopleTypes.PEOPLE_PERSON,
	// PeopleNames.PEOPLE_LAST_NAME,
	// PeopleNames.PEOPLE_PRIMARY_EMAIL));
	// personViewer.setInput(persons);
	// } catch (RepositoryException e) {
	// throw new PeopleException("Unable to list persons", e);
	// }
	// }
	//
	// /** Build repository request */
	// private NodeIterator doSearch(Session session, String filter,
	// String typeName, String orderProperty, String orderProperty2)
	// throws RepositoryException {
	// QueryManager queryManager = session.getWorkspace().getQueryManager();
	// QueryObjectModelFactory factory = queryManager.getQOMFactory();
	//
	// Selector source = factory.selector(typeName, typeName);
	//
	// // no Default Constraint
	// Constraint defaultC = null;
	//
	// // Parse the String
	// String[] strs = filter.trim().split(" ");
	// if (strs.length == 0) {
	// // StaticOperand so = factory.literal(session.getValueFactory()
	// // .createValue("*"));
	// // defaultC = factory.fullTextSearch("selector", null, so);
	// } else {
	// for (String token : strs) {
	// StaticOperand so = factory.literal(session.getValueFactory()
	// .createValue("*" + token + "*"));
	// Constraint currC = factory.fullTextSearch(
	// source.getSelectorName(), null, so);
	// if (defaultC == null)
	// defaultC = currC;
	// else
	// defaultC = factory.and(defaultC, currC);
	// }
	// }
	//
	// Ordering order = null, order2 = null;
	//
	// if (orderProperty != null && !"".equals(orderProperty.trim()))
	// order = factory.ascending(factory.lowerCase(factory.propertyValue(
	// source.getSelectorName(), orderProperty)));
	// if (orderProperty2 != null && !"".equals(orderProperty2.trim()))
	// order2 = factory.ascending(factory.propertyValue(
	// source.getSelectorName(), orderProperty2));
	//
	// QueryObjectModel query;
	// if (order == null) {
	// query = factory.createQuery(source, defaultC, null, null);
	// } else {
	// if (order2 == null)
	// query = factory.createQuery(source, defaultC,
	// new Ordering[] { order }, null);
	// else
	// query = factory.createQuery(source, defaultC, new Ordering[] {
	// order, order2 }, null);
	// }
	// query.setLimit(ROW_LIMIT.longValue());
	// QueryResult result = query.execute();
	// return result.getNodes();
	// }

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		try {
			session = repository.login();
		} catch (RepositoryException e) {
			throw new AkbException("Unable to initialize "
					+ "session for view " + ID, e);
		}
	}
}