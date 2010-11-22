package org.argeo.slc.client.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

public class ResultListView extends ViewPart {
	private final static Log log = LogFactory.getLog(ResultListView.class);

	public static final String ID = "org.argeo.slc.client.ui.resultListView";

	private final static String DISPLAY_CMD_ID = "org.argeo.slc.client.ui.displayResultDetails";
	private final static String DISPLAY_AS_XLS_CMD_ID = "com.capco.sparta.client.ui.displayResultDetailsWithExcel";
	private final static String SAVE_AS_XLS_CMD_ID = "com.capco.sparta.client.ui.saveResultAsExcelFile";
	private final static String UUID_PARAM_ID = "org.argeo.slc.client.commands.resultUuid";
	private final static String NAME_PARAM_ID = "org.argeo.slc.client.commands.resultName";
	private final static String PLATFORM = SWT.getPlatform();

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"MM/dd/yyyy 'at' HH:mm:ss");

	private TableViewer viewer;
	private TreeTestResultCollectionDao testResultCollectionDao;

	private ResultAttributes selectedRa;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());
		viewer.addSelectionChangedListener(new SelectionChangedListener());

		// create the context menu

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});

		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	protected Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);
		// table.addMouseListener(new RightClickListener());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Test Case");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Close Date");
		column.setWidth(120);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("UUID");
		column.setWidth(300);

		return table;
	}

	// TODO : Improve this methods.
	// For now it is a workaround because we cannot dynamically update context
	// menu to pass the UUID as command parameter
	// public String[] getSelectedResult() {
	// Object obj = ((IStructuredSelection) viewer.getSelection())
	// .getFirstElement();
	//
	// String[] attributes = new String[2];
	//
	// if (obj == null || !(obj instanceof ResultAttributes))
	// return null;
	// else {
	// ResultAttributes ra = (ResultAttributes) obj;
	// attributes[0] = ra.getUuid();
	// attributes[1] = (ra.getAttributes().get("testCase") == null) ? null
	// : ra.getAttributes().get("testCase");
	// return attributes;
	// }
	// }

	// View Specific inner class

	// Handle Events
	class SelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent evt) {

			IStructuredSelection curSelection = (IStructuredSelection) evt
					.getSelection();
			Object obj = curSelection.getFirstElement();

			if (obj instanceof ResultAttributes) {
				selectedRa = (ResultAttributes) obj;
			}
		}
	}

	private void refreshCommand(IMenuManager menuManager,
			IServiceLocator locator, String cmdId, String label, String iconPath) {
		IContributionItem ici = menuManager.find(cmdId);
		if (ici != null)
			menuManager.remove(ici);
		CommandContributionItemParameter contributionItemParameter = new CommandContributionItemParameter(
				locator, null, cmdId, SWT.PUSH);

		// Set Params
		contributionItemParameter.label = label;
		contributionItemParameter.icon = ClientUiPlugin
				.getImageDescriptor(iconPath);

		Map<String, String> params = new HashMap<String, String>();
		params.put(UUID_PARAM_ID, selectedRa.getUuid());
		params.put(NAME_PARAM_ID,
				(selectedRa.getAttributes().get("testCase") == null) ? null
						: selectedRa.getAttributes().get("testCase"));
		contributionItemParameter.parameters = params;

		CommandContributionItem cci = new CommandContributionItem(
				contributionItemParameter);
		cci.setId(cmdId);
		menuManager.add(cci);
	}

	private void contextMenuAboutToShow(IMenuManager menuManager) {

		IWorkbenchWindow window = ClientUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();

		refreshCommand(menuManager, window, DISPLAY_CMD_ID,
				"Display as a tree", "icons/result_details.gif");
		// We only show this command on windows OS
		if (PLATFORM.equals("win32") || PLATFORM.equals("wpf")) {
			refreshCommand(menuManager, window, DISPLAY_AS_XLS_CMD_ID,
					"Display with Excel", "icons/excel.png");
		}
		refreshCommand(menuManager, window, SAVE_AS_XLS_CMD_ID,
				"Save as Excel file", "icons/excel.png");
	}

	// Providers

	protected static class ViewContentProvider implements
			IStructuredContentProvider {

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object obj) {
			if (obj instanceof List) {
				return ((List<ResultAttributes>) obj).toArray();
			} else {
				return new Object[0];
			}
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			ResultAttributes ra = (ResultAttributes) obj;
			switch (index) {
			case 0:
				return (ra.getAttributes().get("testCase") == null) ? null : ra
						.getAttributes().get("testCase");
			case 1:
				// otherwise we get null pointer exception when the test is not
				// closed yet.
				return (ra.getCloseDate() == null) ? null : dateFormatter
						.format(ra.getCloseDate());
			case 2:
				return ra.getUuid();
			}
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void retrieveResults() {
		try {
			List<ResultAttributes> lst = testResultCollectionDao
					.listResultAttributes(null);
			if (log.isTraceEnabled())
				log.trace("Result attributes count: " + lst.size());
			viewer.setInput(lst);
			// viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Handle Events

	/**
	 * The ResultAttributes expose a part of the information contained in the
	 * TreeTestResult, It has the same UUID as the corresponding treeTestResult.
	 */
	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();

			if (obj instanceof ResultAttributes) {
				ResultAttributes ra = (ResultAttributes) obj;
				IWorkbench iw = ClientUiPlugin.getDefault().getWorkbench();
				IHandlerService handlerService = (IHandlerService) iw
						.getService(IHandlerService.class);
				try {
					// get the command from plugin.xml
					IWorkbenchWindow window = iw.getActiveWorkbenchWindow();
					ICommandService cmdService = (ICommandService) window
							.getService(ICommandService.class);
					Command cmd = cmdService
							.getCommand("org.argeo.slc.client.ui.displayResultDetails");

					// log.debug("cmd : " + cmd);
					ArrayList<Parameterization> parameters = new ArrayList<Parameterization>();

					// get the parameter
					IParameter iparam = cmd
							.getParameter("org.argeo.slc.client.commands.resultUuid");

					Parameterization params = new Parameterization(iparam,
							ra.getUuid());
					parameters.add(params);

					// build the parameterized command
					ParameterizedCommand pc = new ParameterizedCommand(cmd,
							parameters.toArray(new Parameterization[parameters
									.size()]));

					// execute the command
					handlerService = (IHandlerService) window
							.getService(IHandlerService.class);
					handlerService.executeCommand(pc, null);

				} catch (Exception e) {
					e.printStackTrace();
					throw new SlcException("Problem while rendering result. "
							+ e.getMessage());
				}
			}
		}
	}

	// IoC
	public void setTestResultCollectionDao(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}
}
