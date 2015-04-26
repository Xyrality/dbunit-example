package er.movies.dbunit;

import java.io.File;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.AbstractTableMetaData;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Before;

import com.webobjects.appserver.WOApplication;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.development.NSBundleFactory;
import com.webobjects.foundation.development.NSStandardProjectBundle;

import er.extensions.ERXExtensions;
import er.extensions.appserver.ERXApplication;
import er.extensions.eof.ERXConstant;
import er.extensions.eof.ERXModelGroup;
import er.extensions.eof.ERXObjectStoreCoordinator;
import er.movies.Application;
import er.movies.TestApplication;

public abstract class MoviesDBTestCase extends DBTestCase
{
	public static final String	dbUrl					= "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	public static final String	dbUsername				= "sa";
	public static final String	dbPassword				= "";
	public static final String	dbDriverClass			= "org.h2.Driver";
	public static final String	erprototype				= "H2PlugIn";

	public static final String	resourcesPath			= "TestResources/";

	private static boolean		applicationInitialized	= false;

	public MoviesDBTestCase(String name) throws Exception
	{
		super(name);
		NSBundleFactory.registerBundleFactory(new NSStandardProjectBundle.Factory());
		Application.reorderClasspath(Application.class);

		TestApplication.setInitMemcache(false);
		TestApplication.setUpdateModelSettings(true);
		TestApplication.setUseInMemoryDB(true);

		configureApplication();

		TestApplication.setStartupDataset(startupDataset());

		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, dbDriverClass);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, dbUrl);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, dbUsername);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, dbPassword);
		if (!applicationInitialized)
		{
			initApplication();
		}

	}

	/**
	 * Override this method to change the base configuration of the test application
	 * @throws Exception
	 */
	public void configureApplication() throws Exception
	{

	}

	/**
	 * Defines a dataset which has to be loaded before the application is started.
	 *
	 * @return the dataset to be loaded
	 * @throws Exception
	 */
	public IDataSet startupDataset() throws Exception
	{
		//@formatter:off
		IDataSet[] datasets = new IDataSet[] {
			     new FlatXmlDataSetBuilder().build(createScenarioSetupFile("setup.xml"))
		};
		//@formatter:on

		return new CompositeDataSet(datasets);
	}

	@Override
	protected IDataSet getDataSet() throws Exception
	{
		// TODO Auto-generated method stub
		return startupDataset();
	}

	/**
	 * @param aName: fileName
	 * @return
	 */
	private File createScenarioSetupFile(final String aName)
	{
		return new File(resourcesPath + "scenarios/" + aName);
	}

	public void initApplication() throws UnknownHostException, InterruptedException
	{
		WOApplication._wasMainInvoked = true;

		NSNotificationCenter.defaultCenter().addObserver(this, new NSSelector<NSNotification>("didFinishInitialization", ERXConstant.NotificationClassArray), ERXApplication.ApplicationDidFinishInitializationNotification, null);
		Thread applicationThread = new Thread(new ApplicationStarter());
		applicationThread.start();
		synchronized (this)
		{
			while (!applicationInitialized)
			{
				wait();
			}
		}

		Logger.getLogger(AbstractTableMetaData.class.getName()).setLevel(Level.ERROR);

	}

	public void didFinishInitialization(NSNotification aNotification)
	{
		synchronized (this)
		{
			applicationInitialized = true;
			notify();
		}
	}

	private class ApplicationStarter
			implements
			Runnable
	{
		@Override
		public void run()
		{
			//@formatter:off
			ERXExtensions.initApp(TestApplication.class, new String[] {
					"-WOAdaptor",					"WODefaultAdaptor",
					"-WOPort",						"-1",
					"-WOAutoOpenInBrowser",			"false"
			});
			//@formatter:on
		}
	}

	@AfterClass
	public void removeObserver()
	{
		NSNotificationCenter.defaultCenter().removeObserver(this);
	}

	@Before
	public void setupTest()
	{
	}

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception
	{
		return DatabaseOperation.CLEAN_INSERT;
	}

	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception
	{
		return DatabaseOperation.DELETE_ALL;
	}

	/**
	 * Loads scenario defined by getDataSet
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * removes all objects from the database
	 */
	@Override
	public void tearDown() throws Exception
	{
		ERXObjectStoreCoordinator.defaultCoordinator().invalidateAllObjects();

		Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		Statement statement = connection.createStatement();

		// disable checking of foreign keys
		statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");
		for (EOModel model : ERXModelGroup.defaultGroup().models())
		{
			for (EOEntity entity : model.entities())
			{
				if (entity.externalName() != null)
				{
					DatabaseMetaData dbm = connection.getMetaData();
					// check if the table is there
					final ResultSet tables = dbm.getTables("TEST", "PUBLIC", entity.externalName().toUpperCase(), null);
					if (tables.next())
					{
						// truncate if exists
						statement.executeUpdate("TRUNCATE TABLE " + entity.externalName() + ";");
					}
				}

			}
		}
		// enable checking of foreign keys
		statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");

		super.tearDown();
	}
}
