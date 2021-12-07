package org.argeo.cms.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.cms.CmsException;
import org.argeo.osgi.transaction.WorkTransaction;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdmin;

/**
 * Register one or many roles via a user admin service. Does nothing if the role
 * is already registered.
 */
public class SimpleRoleRegistration implements Runnable {
	private final static Log log = LogFactory.getLog(SimpleRoleRegistration.class);

	private String role;
	private List<String> roles = new ArrayList<String>();
	private UserAdmin userAdmin;
	private WorkTransaction userTransaction;

	@Override
	public void run() {
		try {
			userTransaction.begin();
			if (role != null && !roleExists(role))
				newRole(toDn(role));

			for (String r : roles)
				if (!roleExists(r))
					newRole(toDn(r));
			userTransaction.commit();
		} catch (Exception e) {
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				log.error("Cannot rollback", e1);
			}
			throw new CmsException("Cannot add roles", e);
		}
	}

	private boolean roleExists(String role) {
		return userAdmin.getRole(toDn(role).toString()) != null;
	}

	protected void newRole(LdapName r) {
		userAdmin.createRole(r.toString(), Role.GROUP);
		log.info("Added role " + r + " required by application.");
	}

	public void register(UserAdmin userAdminService, Map<?, ?> properties) {
		this.userAdmin = userAdminService;
		run();
	}

	protected LdapName toDn(String name) {
		try {
			return new LdapName("cn=" + name + ",ou=roles,ou=node");
		} catch (InvalidNameException e) {
			throw new CmsException("Badly formatted role name " + name, e);
		}
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setUserAdmin(UserAdmin userAdminService) {
		this.userAdmin = userAdminService;
	}

	public void setUserTransaction(WorkTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

}
