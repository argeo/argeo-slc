/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.hibernate.unit;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.argeo.slc.support.deploy.db.DbModel;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Creates a relational data model from Hibernate mapping files. The benefit of
 * this class is to be able to use Hibernate to have test data which are
 * independent from the type of database used.
 */
public class DbModelHibernate implements DbModel {
	private String dialect;
	private List<String> mappings;

	/** Sets the Hibernate dialect to use. */
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	/** Sets the list of mappings to consider. */
	public void setMappings(List<String> mappings) {
		this.mappings = mappings;
	}

	/**
	 * Creates an Hibernate schema export tool, in order to create the
	 * underlying datamodel.
	 */
	protected SchemaExport createSchemaExport(Connection connection) {
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

	public void createSchema(Connection connection) {
		SchemaExport schemaExport = createSchemaExport(connection);
		schemaExport.create(true, true);
	}

}
