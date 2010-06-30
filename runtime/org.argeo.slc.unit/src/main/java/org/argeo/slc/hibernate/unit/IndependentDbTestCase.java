/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Helper to make db vendor independent tests using DbUnit data sets. Based on
 * {@link DbModelHibernate}.
 */
public abstract class IndependentDbTestCase extends AbstractSpringTestCase {
	private IDatabaseTester databaseTester;

	/** Creates the DDL of the data model and loads the data. */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		databaseTester = new DataSourceDatabaseTester(getDataSource());
		databaseTester.setSetUpOperation(new DatabaseOperation() {

			@Override
			public void execute(IDatabaseConnection connection, IDataSet dataSet)
					throws DatabaseUnitException, SQLException {
				DbModelHibernate dbModel = getDbModel();
				SchemaExport schemaExport = dbModel
						.createSchemaExport(connection.getConnection());
				schemaExport.create(true, true);

				DatabaseOperation.INSERT.execute(connection, dataSet);
			}

		});
		databaseTester.setDataSet(createDataSet());
		databaseTester.onSetup();
	}

	@Override
	protected void tearDown() throws Exception {
		if (databaseTester != null) {
			databaseTester.onTearDown();
		}
		super.tearDown();
	}

	/**
	 * The data source to use. The default implementation returns a bean named
	 * {@link #getDataSourceBeanName}
	 */
	protected DataSource getDataSource() {
		return getBean(DataSource.class);
	}

	/**
	 * Creates the data set to use. The default implementation creates a
	 * <code>FlatXmlDataSet</code> load from the resource defined in
	 * {@link #getDataSetResource()}
	 */
	protected IDataSet createDataSet() {
		InputStream in = null;
		try {
			in = getDataSetResource().getInputStream();
			String[] replaceStrings = getReplacementStrings();
			IDataSet dataSet;
			if (replaceStrings.length == 0) {
				dataSet = new FlatXmlDataSet(in);
			} else {
				dataSet = new ReplacementDataSet(new FlatXmlDataSet(in));
				for (String str : replaceStrings) {
					replace((ReplacementDataSet) dataSet, str);
				}
			}
			return dataSet;
		} catch (Exception e) {
			throw new SlcException("Cannot create data set", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * To be overridden. Return an empty array by default.
	 * 
	 * @return the array of strings to replace in the dataset
	 */
	protected String[] getReplacementStrings() {
		return new String[0];
	}

	/**
	 * Set the object replacing the given string. To be overridden. Does nothing
	 * by default.
	 */
	protected void replace(ReplacementDataSet dataSet, String str)
			throws Exception {

	}

	/**
	 * Replace the given string by the content of the resource with the same
	 * name in the same package, as a byte array.
	 */
	protected void replaceByRessource(ReplacementDataSet dataSet, String str)
			throws Exception {
		Resource zipResource = new ClassPathResource(inPackage(str));

		dataSet.addReplacementObject(str, IOUtils.toByteArray(zipResource
				.getInputStream()));
	}

	/**
	 * The resource of the data set to load. The default implementation loads a
	 * <code>ClassPathResource</code> located at
	 * {@link #getDataSetResourceLocation()}.
	 */
	protected Resource getDataSetResource() {
		return new ClassPathResource(getDataSetResourceLocation());
	}

	/**
	 * The location of the data set to load. The default implementation loads
	 * <i>dataSet.xml</i> found in the same package as the test.
	 */
	protected String getDataSetResourceLocation() {
		return inPackage("dataSet.xml");
	}

	/**
	 * The DB model to us to create the DDL of the testes database. The default
	 * implementation loads a bean named after {@link #getDbModelBeanName()}.
	 */
	protected DbModelHibernate getDbModel() {
		return (DbModelHibernate) getContext().getBean(getDbModelBeanName());
	}

	/**
	 * The name of the bean to load. The default implementation returns
	 * <i>dbModel</i>.
	 */
	protected String getDbModelBeanName() {
		return "dbModel";
	}
}
