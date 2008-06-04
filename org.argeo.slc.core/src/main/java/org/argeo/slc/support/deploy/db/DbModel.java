package org.argeo.slc.support.deploy.db;

import java.sql.Connection;

public interface DbModel {
	public void createSchema(Connection connection);
}
