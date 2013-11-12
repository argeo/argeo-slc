package org.argeo.slc.akb.ui.editors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import org.argeo.security.ui.PrivilegedJob;
import org.argeo.slc.SlcException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/** Test JDBC. */
public class JdbcTestPage extends FormPage implements AkbNames {
	private Node node;
	private AkbService akbService;

	private TableViewer viewer = null;
	private IContentProvider contentProvider;

	private PreparedStatement statement;

	public JdbcTestPage(AkbService akbService, FormEditor editor, String id,
			String title, Node node) {
		super(editor, id, title);
		this.akbService = akbService;
		this.node = node;
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite parent = form.getBody();

		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		FormToolkit toolkit = getEditor().getToolkit();
		Table table = toolkit.createTable(parent, SWT.VIRTUAL);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer = new TableViewer(table);
		contentProvider = new JdbcTestContentProvider(viewer);
		viewer.setContentProvider(contentProvider);
		// viewer.setLabelProvider(new ColumnLabelProvider(){});

		statement = akbService.prepareJdbcQuery(node);
		PrivilegedJob job = new PrivilegedJob("Execute query on " + node) {

			@Override
			protected IStatus doRun(IProgressMonitor progressMonitor) {
				try {
					final ResultSet resultSet = statement.executeQuery();
					getEditorSite().getWorkbenchWindow().getShell()
							.getDisplay().syncExec(new Runnable() {

								@Override
								public void run() {
									viewer.setInput(resultSet);
								}
							});
					return Status.OK_STATUS;
				} catch (SQLException e) {
					throw new SlcException("Cannot execute " + node, e);
				}
			}
		};
		job.schedule();
	}

	@Override
	public void dispose() {
		try {
			statement.close();
			statement.getConnection().close();
		} catch (SQLException e) {
			// silent
		}
	}

	private class JdbcTestContentProvider implements ILazyContentProvider {
		private TableViewer viewer;
		private ResultSet resultSet;
		private boolean isScrollable;

		private List<Object> buffer = new ArrayList<Object>();

		private List<JdbcColumn> columns = new ArrayList<JdbcColumn>();
		private Integer columnCount = 0;

		private int rowCount = 0;

		public JdbcTestContentProvider(TableViewer viewer) {
			this.viewer = viewer;
		}

		public void dispose() {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// silent
			}
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput == null)
				return;

			TableViewer viewer = (TableViewer) v;

			resultSet = (ResultSet) newInput;
			try {
				isScrollable = resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY;
				columnCount = resultSet.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columns.add(new JdbcColumn(i, resultSet.getMetaData()));
					if (oldInput == null)// first time
						addColumn(viewer, i - 1);
				}

				if (isScrollable) {
					if (resultSet.next())
						rowCount = 1;
					viewer.setItemCount(rowCount);
				} else {
					while (resultSet.next()) {
						Object[] lst = new Object[columnCount];
						for (int i = 1; i <= columnCount; i++) {
							lst[i - 1] = resultSet.getObject(i);
							buffer.add(lst);
						}
					}
					viewer.setItemCount(buffer.size());
				}

			} catch (SQLException e) {
				throw new SlcException("Cannot configure JDBC column", e);
			}
		}

		protected void addColumn(TableViewer viewer, final int index) {
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			col.getColumn().setText(columns.get(index).name);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Object obj = ((Object[]) element)[index];
					if (obj == null)
						return null;
					return obj.toString();
				}
			});
		}

		public void updateElement(int index) {
			if (resultSet == null)
				return;

			try {
				if (isScrollable) {
					resultSet.absolute(index + 1);
					Object[] lst = new Object[columnCount];
					for (int i = 1; i <= columnCount; i++) {
						lst[i - 1] = resultSet.getObject(i);
					}
					viewer.replace(lst, index);
					int itemCount = viewer.getTable().getItemCount();
					if (index == (itemCount - 1) && resultSet.next())
						viewer.setItemCount(itemCount + 1);
				} else {
					viewer.replace(buffer.get(index), index);
				}
			} catch (Exception e) {
				throw new SlcException("Cannot update element", e);
			}
		}
	}

	private class JdbcColumn {
		// public final Integer index;
		public final String name;

		// public final Integer type;

		public JdbcColumn(int index, ResultSetMetaData metaData)
				throws SQLException {
			// this.index = index;
			this.name = metaData.getColumnName(index);
			// this.type = metaData.getColumnType(index);

		}
	}

}
