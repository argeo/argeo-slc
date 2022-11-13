package org.argeo.cms.e4.users.providers;

import static org.argeo.eclipse.ui.EclipseUiUtils.notEmpty;

import org.argeo.api.acr.ldap.LdapAttr;
import org.argeo.api.cms.CmsConstants;
import org.argeo.cms.auth.UserAdminUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.service.useradmin.User;

/**
 * Filter user list using JFace mechanism on the client (yet on the server) side
 * rather than having the UserAdmin to process the search
 */
public class UserFilter extends ViewerFilter {
	private static final long serialVersionUID = 5082509381672880568L;

	private String searchString;
	private boolean showSystemRole = true;

	private final String[] knownProps = { LdapAttr.DN, LdapAttr.cn.name(), LdapAttr.givenName.name(),
			LdapAttr.sn.name(), LdapAttr.uid.name(), LdapAttr.description.name(), LdapAttr.mail.name() };

	public void setSearchText(String s) {
		// ensure that the value can be used for matching
		if (notEmpty(s))
			searchString = ".*" + s.toLowerCase() + ".*";
		else
			searchString = ".*";
	}

	public void setShowSystemRole(boolean showSystemRole) {
		this.showSystemRole = showSystemRole;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		User user = (User) element;
		if (!showSystemRole && user.getName().matches(".*(" + CmsConstants.SYSTEM_ROLES_BASEDN + ")"))
			// UserAdminUtils.getProperty(user, LdifName.dn.name())
			// .toLowerCase().endsWith(AuthConstants.ROLES_BASEDN))
			return false;

		if (searchString == null || searchString.length() == 0)
			return true;

		if (user.getName().matches(searchString))
			return true;

		for (String key : knownProps) {
			String currVal = UserAdminUtils.getProperty(user, key);
			if (notEmpty(currVal) && currVal.toLowerCase().matches(searchString))
				return true;
		}
		return false;
	}
}
