package org.argeo.cms.e4.users;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.argeo.api.acr.ldap.LdapAttr;
import org.argeo.api.acr.ldap.LdapObj;
import org.argeo.api.cms.CmsConstants;
import org.argeo.api.cms.CmsLog;
import org.argeo.cms.CurrentUser;
import org.argeo.cms.e4.users.providers.CommonNameLP;
import org.argeo.cms.e4.users.providers.DomainNameLP;
import org.argeo.cms.e4.users.providers.RoleIconLP;
import org.argeo.cms.e4.users.providers.UserDragListener;
import org.argeo.cms.swt.CmsException;
import org.argeo.cms.swt.useradmin.LdifUsersTable;
//import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
//import org.argeo.cms.ui.workbench.internal.useradmin.UiUserAdminListener;
//import org.argeo.cms.ui.workbench.internal.useradmin.UserAdminWrapper;
//import org.argeo.cms.ui.workbench.internal.useradmin.providers.CommonNameLP;
//import org.argeo.cms.ui.workbench.internal.useradmin.providers.DomainNameLP;
//import org.argeo.cms.ui.workbench.internal.useradmin.providers.RoleIconLP;
//import org.argeo.cms.ui.workbench.internal.useradmin.providers.UserDragListener;
//import org.argeo.cms.ui.workbench.internal.useradmin.providers.UserTableDefaultDClickListener;
import org.argeo.eclipse.ui.ColumnDefinition;
import org.argeo.eclipse.ui.EclipseUiUtils;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;

/** List all groups with filter */
public class GroupsView {
	private final static CmsLog log = CmsLog.getLog(GroupsView.class);
	// public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".groupsView";

	@Inject
	private EPartService partService;
	@Inject
	private UserAdminWrapper userAdminWrapper;

	// UI Objects
	private LdifUsersTable groupTableViewerCmp;
	private TableViewer userViewer;
	private List<ColumnDefinition> columnDefs = new ArrayList<ColumnDefinition>();

	private UserAdminListener listener;

	@PostConstruct
	public void createPartControl(Composite parent, ESelectionService selectionService) {
		parent.setLayout(EclipseUiUtils.noSpaceGridLayout());

		// boolean isAdmin = CurrentUser.isInRole(NodeConstants.ROLE_ADMIN);

		// Define the displayed columns
		columnDefs.add(new ColumnDefinition(new RoleIconLP(), "", 19));
		columnDefs.add(new ColumnDefinition(new CommonNameLP(), "Name", 150));
		columnDefs.add(new ColumnDefinition(new DomainNameLP(), "Domain", 100));
		// Only show technical DN to admin
		// if (isAdmin)
		// columnDefs.add(new ColumnDefinition(new UserNameLP(),
		// "Distinguished Name", 300));

		// Create and configure the table
		groupTableViewerCmp = new MyUserTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		groupTableViewerCmp.setColumnDefinitions(columnDefs);
		// if (isAdmin)
		// groupTableViewerCmp.populateWithStaticFilters(false, false);
		// else
		groupTableViewerCmp.populate(true, false);

		groupTableViewerCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Links
		userViewer = groupTableViewerCmp.getTableViewer();
		userViewer.addDoubleClickListener(new UserTableDefaultDClickListener(partService));
		// getViewSite().setSelectionProvider(userViewer);
		userViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				selectionService.setSelection(selection.toList());
			}
		});

		// Really?
		groupTableViewerCmp.refresh();

		// Drag and drop
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		userViewer.addDragSupport(operations, tt, new UserDragListener(userViewer));

		// // Register a useradmin listener
		// listener = new UserAdminListener() {
		// @Override
		// public void roleChanged(UserAdminEvent event) {
		// if (userViewer != null && !userViewer.getTable().isDisposed())
		// refresh();
		// }
		// };
		// userAdminWrapper.addListener(listener);
		// }

		// Register a useradmin listener
		listener = new MyUiUAListener(parent.getDisplay());
		userAdminWrapper.addListener(listener);
	}

	private class MyUiUAListener extends UiUserAdminListener {
		public MyUiUAListener(Display display) {
			super(display);
		}

		@Override
		public void roleChangedToUiThread(UserAdminEvent event) {
			if (userViewer != null && !userViewer.getTable().isDisposed())
				refresh();
		}
	}

	private class MyUserTableViewer extends LdifUsersTable {
		private static final long serialVersionUID = 8467999509931900367L;

		private boolean showSystemRoles = true;

		private final String[] knownProps = { LdapAttr.uid.name(), LdapAttr.cn.name(), LdapAttr.DN };

		public MyUserTableViewer(Composite parent, int style) {
			super(parent, style);
			showSystemRoles = CurrentUser.isInRole(CmsConstants.ROLE_ADMIN);
		}

		protected void populateStaticFilters(Composite staticFilterCmp) {
			staticFilterCmp.setLayout(new GridLayout());
			final Button showSystemRoleBtn = new Button(staticFilterCmp, SWT.CHECK);
			showSystemRoleBtn.setText("Show system roles");
			showSystemRoles = CurrentUser.isInRole(CmsConstants.ROLE_ADMIN);
			showSystemRoleBtn.setSelection(showSystemRoles);

			showSystemRoleBtn.addSelectionListener(new SelectionAdapter() {
				private static final long serialVersionUID = -7033424592697691676L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					showSystemRoles = showSystemRoleBtn.getSelection();
					refresh();
				}

			});
		}

		@Override
		protected List<User> listFilteredElements(String filter) {
			Role[] roles;
			try {
				StringBuilder builder = new StringBuilder();
				StringBuilder tmpBuilder = new StringBuilder();
				if (EclipseUiUtils.notEmpty(filter))
					for (String prop : knownProps) {
						tmpBuilder.append("(");
						tmpBuilder.append(prop);
						tmpBuilder.append("=*");
						tmpBuilder.append(filter);
						tmpBuilder.append("*)");
					}
				if (tmpBuilder.length() > 1) {
					builder.append("(&(").append(LdapAttr.objectClass.name()).append("=")
							.append(LdapObj.groupOfNames.name()).append(")");
					// hide tokens
					builder.append("(!(").append(LdapAttr.DN).append("=*").append(CmsConstants.TOKENS_BASEDN)
							.append("))");

					if (!showSystemRoles)
						builder.append("(!(").append(LdapAttr.DN).append("=*").append(CmsConstants.SYSTEM_ROLES_BASEDN)
								.append("))");
					builder.append("(|");
					builder.append(tmpBuilder.toString());
					builder.append("))");
				} else {
					if (!showSystemRoles)
						builder.append("(&(").append(LdapAttr.objectClass.name()).append("=")
								.append(LdapObj.groupOfNames.name()).append(")(!(").append(LdapAttr.DN).append("=*")
								.append(CmsConstants.SYSTEM_ROLES_BASEDN).append("))(!(").append(LdapAttr.DN).append("=*")
								.append(CmsConstants.TOKENS_BASEDN).append(")))");
					else
						builder.append("(&(").append(LdapAttr.objectClass.name()).append("=")
								.append(LdapObj.groupOfNames.name()).append(")(!(").append(LdapAttr.DN).append("=*")
								.append(CmsConstants.TOKENS_BASEDN).append(")))");

				}
				roles = userAdminWrapper.getUserAdmin().getRoles(builder.toString());
			} catch (InvalidSyntaxException e) {
				throw new CmsException("Unable to get roles with filter: " + filter, e);
			}
			List<User> users = new ArrayList<User>();
			for (Role role : roles)
				if (!users.contains(role))
					users.add((User) role);
				else
					log.warn("Duplicated role: " + role);

			return users;
		}
	}

	public void refresh() {
		groupTableViewerCmp.refresh();
	}

	@PreDestroy
	public void dispose() {
		userAdminWrapper.removeListener(listener);
	}

	@Focus
	public void setFocus() {
		groupTableViewerCmp.setFocus();
	}

	/* DEPENDENCY INJECTION */
	public void setUserAdminWrapper(UserAdminWrapper userAdminWrapper) {
		this.userAdminWrapper = userAdminWrapper;
	}
}
