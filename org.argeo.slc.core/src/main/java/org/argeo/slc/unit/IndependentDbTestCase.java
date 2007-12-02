package org.argeo.slc.unit;

import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.hibernate.tool.hbm2ddl.SchemaExport;

import org.argeo.slc.core.SlcException;

/**
 * Helper to make db vendor independent tests using DbUnit data sets. Based on
 * {@link DbModel}.
 */
public abstract class IndependentDbTestCase extends SpringBasedTestCase {
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
				DbModel dbModel = getDbModel();
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
		return (DataSource) getContext().getBean(getDataSourceBeanName());
	}

	/**
	 * The name of the data source bean to use. The default implementation
	 * returns <i>dataSource</i>.
	 */
	protected String getDataSourceBeanName() {
		return "dataSource";
	}

	/**
	 * Creates the data set to use. The default implementation creates a
	 * <code>FlatXmlDataSet</code> load from the resource defined in
	 * {@link #getDataSetResource()}
	 */
	protected IDataSet createDataSet() {
		try {
			InputStream in = getDataSetResource().getInputStream();
			IDataSet dataSet = new FlatXmlDataSet(in);
			in.close();
			return dataSet;
		} catch (Exception e) {
			throw new SlcException("Cannot create data set", e);
		}
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
	protected DbModel getDbModel() {
		return (DbModel) getContext().getBean(getDbModelBeanName());
	}

	/**
	 * The name of the bean to load. The default implementation returns
	 * <i>dbModel</i>.
	 */
	protected String getDbModelBeanName() {
		return "dbModel";
	}
}
