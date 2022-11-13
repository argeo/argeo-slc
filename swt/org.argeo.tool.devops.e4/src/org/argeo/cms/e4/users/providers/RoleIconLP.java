package org.argeo.cms.e4.users.providers;

import org.argeo.api.cms.CmsContext;
import org.argeo.api.acr.ldap.LdapAttr;
import org.argeo.api.cms.CmsConstants;
import org.argeo.cms.auth.UserAdminUtils;
import org.argeo.cms.e4.users.SecurityAdminImages;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

/** Provide a bundle specific image depending on the current user type */
public class RoleIconLP extends UserAdminAbstractLP {
	private static final long serialVersionUID = 6550449442061090388L;

	@Override
	public String getText(User user) {
		return "";
	}

	@Override
	public Image getImage(Object element) {
		User user = (User) element;
		String dn = user.getName();
		if (dn.endsWith(CmsConstants.SYSTEM_ROLES_BASEDN))
			return SecurityAdminImages.ICON_ROLE;
		else if (user.getType() == Role.GROUP) {
			String businessCategory = UserAdminUtils.getProperty(user, LdapAttr.businessCategory);
			if (businessCategory != null && businessCategory.equals(CmsContext.WORKGROUP))
				return SecurityAdminImages.ICON_WORKGROUP;
			return SecurityAdminImages.ICON_GROUP;
		} else
			return SecurityAdminImages.ICON_USER;
	}
}
