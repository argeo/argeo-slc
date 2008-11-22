package org.argeo.slc.support.deploy.db;

import javax.sql.DataSource;

public interface JdbcAware {
	public DataSource getDataSource();
}
