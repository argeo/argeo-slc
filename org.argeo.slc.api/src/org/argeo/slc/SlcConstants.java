package org.argeo.slc;

/** Constants useful across all SLC components */
public interface SlcConstants {
	/** Read-write role. */
	public final static String ROLE_SLC = "cn=org.argeo.slc.user,ou=roles,ou=node";

	/** Read only unlogged user */
	public final static String USER_ANONYMOUS = "anonymous";
}
