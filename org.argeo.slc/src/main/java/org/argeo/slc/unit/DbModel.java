package org.argeo.slc.unit;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Creates a relational data model from Hibernate mapping files. The benefit of
 * this class is to be able to use Hibernate to have test data which are
 * independent from the type of database used.
 */
public class DbModel {
	private String dialect;
	private List<String> mappings;

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public void setMappings(List<String> mappings) {
		this.mappings = mappings;
	}

	public SchemaExport createSchemaExport(Connection connection) {
		Configuration configuration = new Configuration();
		Properties properties = new Properties();
		properties.setProperty(Environment.DIALECT, dialect);
		properties.setProperty(Environment.HBM2DDL_AUTO, "create");
		configuration.setProperties(properties);

		for (String mapping : mappings) {
			configuration.addResource(mapping.trim());
		}

		return new SchemaExport(configuration, connection);
	}
}
